package com.sm_four_idiot.backend.service;

import com.sm_four_idiot.backend.domain.RewardLimit;
import com.sm_four_idiot.backend.domain.RewardType;
import com.sm_four_idiot.backend.domain.User;
import com.sm_four_idiot.backend.dto.RewardClaimRequest;
import com.sm_four_idiot.backend.dto.RewardClaimResponse;
import com.sm_four_idiot.backend.dto.RewardLimitsResponse;
import com.sm_four_idiot.backend.exception.RewardLimitExceededException;
import com.sm_four_idiot.backend.repository.RewardLimitRepository;
import com.sm_four_idiot.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;

/**
 * [Back-end] 보상 시스템의 핵심 비즈니스 로직(초기화, 한도 검사, 보상 지급)을 수행하는 서비스 계층입니다.
 */
@Service
@RequiredArgsConstructor
public class RewardService {

    private final RewardLimitRepository rewardLimitRepository;
    private final UserRepository userRepository;

    @Value("#{{'QUEST_COMPLETION': 10, 'WORD_TEST_PERFECT': 5}}")
    private Map<RewardType, Integer> dailyRewardLimits;

    @Transactional(readOnly = true)
    public RewardLimitsResponse getRewardLimits(User user, RewardType rewardType) {
        LocalDate today = LocalDate.now();
        int dailyLimit = dailyRewardLimits.getOrDefault(rewardType, 0);

        RewardLimit rewardLimit = rewardLimitRepository.findByUserAndRewardType(user, rewardType)
                .orElse(null);

        int currentClaimed = 0;
        if (rewardLimit != null && rewardLimit.getLastRewardDate().isEqual(today)) {
            currentClaimed = rewardLimit.getDailyClaimedAmount();
        }

        return RewardLimitsResponse.builder()
                .rewardType(rewardType.name())
                .currentClaimed(currentClaimed)
                .dailyLimit(dailyLimit)
                .isLimitExceeded(currentClaimed >= dailyLimit)
                .build();
    }

    @Transactional
    public RewardClaimResponse claimReward(User user, RewardClaimRequest request) {
        RewardType rewardType = request.getRewardType();
        int requestedAmount = request.getAmount();
        LocalDate today = LocalDate.now();
        int dailyLimit = dailyRewardLimits.getOrDefault(rewardType, 0);

        RewardLimit rewardLimit = rewardLimitRepository.findByUserAndRewardTypeWithLock(user, rewardType)
                .orElseGet(() -> rewardLimitRepository.save(
                        RewardLimit.builder()
                                .user(user)
                                .rewardType(rewardType)
                                .dailyClaimedAmount(0)
                                .lastRewardDate(today)
                                .build()
                ));

        rewardLimit.resetDailyLimitIfNewDay(today);

        if (rewardLimit.getDailyClaimedAmount() + requestedAmount > dailyLimit) {
            throw new RewardLimitExceededException(
                    String.format("일일 보상 한도(%d점)를 초과했습니다. 현재 누적: %d점", dailyLimit, rewardLimit.getDailyClaimedAmount())
            );
        }

        // 4. 보상 지급 및 데이터 업데이트
        rewardLimit.addClaimedAmount(requestedAmount);

        // [Fix 1] 객체 수정(user.addPoints) 대신 DB 직접 업데이트를 통해 OSIV 의존성 탈피 및 증발 버그 해결
        userRepository.incrementUserPoints(user.getId(), requestedAmount);

        return RewardClaimResponse.builder()
                .success(true)
                // user 객체는 Detached 상태이므로 메모리에 있던 값(과거)에 이번에 얻은 값을 더해서 응답합니다.
                .newTotalPoints(user.getPoints() + requestedAmount)
                .claimedAmount(requestedAmount)
                .message("보상 " + requestedAmount + "점이 정상 지급되었습니다.")
                .build();
    }
}