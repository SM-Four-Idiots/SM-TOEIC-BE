package com.sm_four_idiot.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * [Entity] 사용자별 퀘스트 진행 정보를 DB에 매핑하는 엔티티
 * * [Backend DB]
 * - uk_user_quest_type: 동일한 유저가 같은 타입의 퀘스트를 중복으로 가질 수 없도록 방어하는 유니크 인덱스입니다.
 * - 낙관적 락 대신 데이터 무결성을 DB 제약조건으로 1차 방어합니다.
 */
@Entity
@Table(name = "user_quest", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_quest_type", columnNames = {"user_id", "quest_type"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserQuest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 유저와의 다대일(N:1) 매핑. 지연 로딩을 사용하여 불필요한 조회를 막습니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "quest_type", nullable = false)
    private QuestType questType;

    @Column(name = "current_progress", nullable = false)
    private int currentProgress;

    @Column(name = "target_progress", nullable = false)
    private int targetProgress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestStatus status;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * [Backend] 진행도 증가 로직 (도메인 캡슐화)
     * 외부(Service 등)에서 직접 값을 세팅하지 못하게 하고 무결성을 검증합니다.
     */
    // [Fix 3] 도메인 보호 로직 (Validation) 추가
    public void incrementProgress(int amount) {
        // 보안/버그 방지: 클라이언트 변조로 인한 음수 값 유입 원천 차단
        if (amount <= 0) {
            throw new IllegalArgumentException("퀘스트 진행도는 양수여야 합니다.");
        }
        // 이미 완료/보상수령 상태면 더 이상 진행도를 올리지 않음
        if (this.status != QuestStatus.IN_PROGRESS) return;

        // 오버플로우 방지 및 타겟 진행도까지만 증가 (예: 목표가 10인데 15가 되지 않도록 방어)
        this.currentProgress = Math.min(this.currentProgress + amount, this.targetProgress);
        this.updatedAt = LocalDateTime.now();
        
        // 목표치 도달 시 상태를 COMPLETED로 승격
        if (this.currentProgress >= this.targetProgress) {
            this.status = QuestStatus.COMPLETED;
        }
    }

    /**
     * [Backend] 보상 지급 완료 처리 로직
     */
    public void rewardQuest() {
        if (this.status == QuestStatus.COMPLETED) {
            this.status = QuestStatus.REWARDED;
            this.updatedAt = LocalDateTime.now();
        }
    }
}