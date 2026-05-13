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

        // [Fix - 이미지 8, 9] 악의적인 유저가 API로 음수(-)를 보내 일일 한도를 우회하거나 포인트를 깎는 행위 원천 차단
        if (requestedAmount <= 0) {
            throw new IllegalArgumentException("보상 포인트는 1 이상이어야 합니다.");
        }

        LocalDate today = LocalDate.now();
        int dailyLimit = dailyRewardLimits.getOrDefault(rewardType, 0);

        // [Fix - 이미지 3] 동시 다발적인 첫 보상 수령 시 트랜잭션 롤백 오류를 막기 위한 안전한 초기화 (Native Upsert)
        rewardLimitRepository.insertIfNotExists(user.getId(), rewardType.name(), today);

        // 위에서 초기화를 보장했으므로 데이터가 무조건 존재함. 안전하게 쓰기 락(PESSIMISTIC_WRITE) 획득
        RewardLimit rewardLimit = rewardLimitRepository.findByUserAndRewardTypeWithLock(user, rewardType)
                .orElseThrow(() -> new IllegalStateException("보상 데이터를 잠글 수 없습니다."));

        // 자정이 지났다면 누적량을 0으로 리셋
        rewardLimit.resetDailyLimitIfNewDay(today);

        // 한도 초과 검사
        if (rewardLimit.getDailyClaimedAmount() + requestedAmount > dailyLimit) {
            throw new RewardLimitExceededException(
                    String.format("일일 보상 한도(%d점)를 초과했습니다. 현재 누적: %d점", dailyLimit, rewardLimit.getDailyClaimedAmount())
            );
        }

        // 보상 지급 및 엔티티 업데이트
        rewardLimit.addClaimedAmount(requestedAmount);

        // [Fix - 이미지 2] OSIV(Open-Session-In-View)에 의존하여 증발해버리던 user.addPoints() 대신 DB 직접 업데이트
        userRepository.incrementUserPoints(user.getId(), requestedAmount);

        return RewardClaimResponse.builder()
                .success(true)
                // 현재 user 객체는 영속성 컨텍스트 분리 상태(Detached)이므로 로드 시점의 과거 포인트에 획득 포인트를 수동 합산하여 반환
                .newTotalPoints(user.getPoints() + requestedAmount)
                .claimedAmount(requestedAmount)
                .message("보상 " + requestedAmount + "점이 정상 지급되었습니다.")
                .build();
    }
}