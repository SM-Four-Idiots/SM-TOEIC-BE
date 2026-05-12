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

    // application.yaml에 설정된 각 보상 타입별 최대 한도 값을 Map 형태로 가져옵니다.
    @Value("#{{'QUEST_COMPLETION': 10, 'WORD_TEST_PERFECT': 5}}")
    private Map<RewardType, Integer> dailyRewardLimits;

    /**
     * [Front-end] 파라미터로 받은 보상 타입에 대해, 오늘 한도를 얼마나 채웠는지 계산하여 반환합니다.
     * [Back-end] DB 흐름: 데이터 변경이 없으므 로 readOnly 트랜잭션으로 열어 리소스 점유를 최소화하며 SELECT 만 수행합니다.
     */
    @Transactional(readOnly = true)
    public RewardLimitsResponse getRewardLimits(User user, RewardType rewardType) {
        LocalDate today = LocalDate.now();
        int dailyLimit = dailyRewardLimits.getOrDefault(rewardType, 0);

        RewardLimit rewardLimit = rewardLimitRepository.findByUserAndRewardType(user, rewardType)
                .orElse(null);

        int currentClaimed = 0;
        // DB에 기록이 있고, 그 마지막 기록 날짜가 오늘일 경우에만 현재 획득량으로 인정합니다.
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

    /**
     * [Front-end] 실질적인 보상 지급 처리를 담당하며, 완료 후 현재 총 포인트와 획득 성공 메시지를 반환합니다.
     * [Back-end] DB 흐름: @Transactional 안에서 이뤄지므로 로직 수행 중 하나라도 예외가 발생하면 전체 쿼리가 롤백(Rollback)됩니다.
     */
    @Transactional
    public RewardClaimResponse claimReward(User user, RewardClaimRequest request) {
        RewardType rewardType = request.getRewardType();
        int requestedAmount = request.getAmount();
        LocalDate today = LocalDate.now();
        int dailyLimit = dailyRewardLimits.getOrDefault(rewardType, 0);

        // 1. 비관적 락을 사용하여 동시성 이슈 방지
        // DB 흐름: SELECT ... FOR UPDATE로 해당 행에 쓰기 락을 겁니다. 데이터가 없으면 새로 INSERT 합니다.
        RewardLimit rewardLimit = rewardLimitRepository.findByUserAndRewardTypeWithLock(user, rewardType)
                .orElseGet(() -> rewardLimitRepository.save(
                        RewardLimit.builder()
                                .user(user)
                                .rewardType(rewardType)
                                .dailyClaimedAmount(0)
                                .lastRewardDate(today)
                                .build()
                ));

        // 2. 자정 기준 초기화 검증
        // 마지막 보상 날짜가 오늘이 아니면 영속성 컨텍스트(1차 캐시) 내 엔티티의 누적값을 0으로 리셋합니다.
        rewardLimit.resetDailyLimitIfNewDay(today);

        // 3. 상한 초과 검증
        // 현재 누적량과 이번에 요청한 포인트를 합쳐 일일 한도를 넘으면 런타임 예외를 발생시켜 DB 트랜잭션을 중단 및 롤백시킵니다.
        if (rewardLimit.getDailyClaimedAmount() + requestedAmount > dailyLimit) {
            throw new RewardLimitExceededException(
                    String.format("일일 보상 한도(%d점)를 초과했습니다. 현재 누적: %d점", dailyLimit, rewardLimit.getDailyClaimedAmount())
            );
        }

        // 4. 보상 지급 및 데이터 업데이트
        // 엔티티 값을 수정합니다. 
        // DB 흐름: 메서드 종료 시 영속성 컨텍스트의 변경 감지(Dirty Checking)가 동작하여 자동으로 UPDATE 쿼리가 DB로 전송됩니다.
        rewardLimit.addClaimedAmount(requestedAmount);
        user.addPoints(requestedAmount); // User 도메인에 addPoints() 메서드 구현 권장

        // JPA Dirty Checking으로 인해 별도의 save() 호출은 생략 가능
        
        return RewardClaimResponse.builder()
                .success(true)
                .newTotalPoints(user.getPoints())
                .claimedAmount(requestedAmount)
                .message("보상 " + requestedAmount + "점이 정상 지급되었습니다.")
                .build();
    }
}