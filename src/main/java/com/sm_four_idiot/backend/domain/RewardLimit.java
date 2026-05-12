package com.sm_four_idiot.backend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * [Back-end] 사용자별 일일 보상 획득량을 추적하기 위한 데이터베이스 엔티티입니다.
 * DB 흐름: MySQL의 'reward_limit' 테이블과 1:1로 매핑됩니다.
 * 다중 클릭 등으로 동일한 보상 타입 레코드가 중복 생성되는 것을 막기 위해 user_id와 reward_type 조합에 Unique Index를 강제합니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
    name = "reward_limit",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "reward_type"})
    }
)
public class RewardLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 회원이 탈퇴하면 보상 기록도 함께 사라지도록 보통 연관관계가 설정됩니다. (ManyToOne)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Enum 값을 문자열 자체로 DB에 저장하여 가독성을 높입니다.
    @Enumerated(EnumType.STRING)
    @Column(name = "reward_type", nullable = false)
    private RewardType rewardType;

    // 오늘 누적해서 받은 보상 포인트의 합
    @Column(name = "daily_claimed_amount", nullable = false)
    private int dailyClaimedAmount;

    // 보상을 가장 마지막으로 받은 날짜 (이 날짜가 '오늘'과 다르면 포인트를 0으로 초기화합니다)
    @Column(name = "last_reward_date", nullable = false)
    private LocalDate lastRewardDate;

    @Builder
    public RewardLimit(User user, RewardType rewardType, int dailyClaimedAmount, LocalDate lastRewardDate) {
        this.user = user;
        this.rewardType = rewardType;
        this.dailyClaimedAmount = dailyClaimedAmount;
        this.lastRewardDate = lastRewardDate;
    }

    /**
     * [Back-end] 비즈니스 로직 - 자정 기준 초기화
     * DB 흐름: SELECT해온 이 엔티티의 상태를 변경합니다. 트랜잭션이 끝날 때 JPA의 Dirty Checking에 의해 UPDATE 쿼리가 날아갑니다.
     */
    public void resetDailyLimitIfNewDay(LocalDate today) {
        if (!this.lastRewardDate.isEqual(today)) {
            this.dailyClaimedAmount = 0;
            this.lastRewardDate = today;
        }
    }

    /**
     * [Back-end] 비즈니스 로직 - 보상 누적
     * DB 흐름: 지급된 포인트만큼 엔티티의 누적량을 증가시킵니다. 트랜잭션 종료 시 UPDATE 반영됩니다.
     */
    public void addClaimedAmount(int amount) {
        this.dailyClaimedAmount += amount;
    }
}