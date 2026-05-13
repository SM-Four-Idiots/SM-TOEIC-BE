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

@Service
@RequiredArgsConstructor
public class RewardService {

    private final RewardLimitRepository rewardLimitRepository;
    private final UserRepository userRepository;

    @Value("#{{'QUEST_COMPLETION': 10, 'WORD_TEST_PERFECT': 5}}")
    private Map<RewardType, Integer> dailyRewardLimits;

    @Transactional(readOnly = true)
    public RewardLimitsResponse getRewardLimits(User user, RewardType rewardType) {
        // ... (이전과 동일) ...
    }

    @Transactional
    public RewardClaimResponse claimReward(User user, RewardClaimRequest request) {
        RewardType rewardType = request.getRewardType();
        int requestedAmount = request.getAmount();
        LocalDate today = LocalDate.now();
        int dailyLimit = dailyRewardLimits.getOrDefault(rewardType, 0);

        // [Fix - 이미지 3] 동시 다발적인 첫 보상 수령 시 유니크 제약조건 위반을 INSERT IGNORE로 안전하게 방어
        rewardLimitRepository.insertIfNotExists(user.getId(), rewardType.name(), today);

        // 이후 안전하게 락을 걸고 조회
        RewardLimit rewardLimit = rewardLimitRepository.findByUserAndRewardTypeWithLock(user, rewardType)
                .orElseThrow(() -> new IllegalStateException("보상 데이터를 잠글 수 없습니다."));

        rewardLimit.resetDailyLimitIfNewDay(today);

        if (rewardLimit.getDailyClaimedAmount() + requestedAmount > dailyLimit) {
            throw new RewardLimitExceededException(
                    String.format("일일 보상 한도(%d점)를 초과했습니다. 현재 누적: %d점", dailyLimit, rewardLimit.getDailyClaimedAmount())
            );
        }

        rewardLimit.addClaimedAmount(requestedAmount);

        // [Fix - 이미지 2] OSIV에 의존하는 user.addPoints() 대신 DB 직접 업데이트
        userRepository.incrementUserPoints(user.getId(), requestedAmount);

        return RewardClaimResponse.builder()
                .success(true)
                .newTotalPoints(user.getPoints() + requestedAmount) // Detached 상태이므로 수동 덧셈
                .claimedAmount(requestedAmount)
                .message("보상 " + requestedAmount + "점이 정상 지급되었습니다.")
                .build();
    }
}