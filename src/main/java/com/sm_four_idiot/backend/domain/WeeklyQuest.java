package com.sm_four_idiot.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 주간 퀘스트 엔티티
 * - 매주 일요일 00시 기준으로 초기화 (Lazy 방식)
 * - 일일 퀘스트 7회 완료 시 REWARDED 처리
 */
@Entity
@Table(name = "weekly_quest",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "week_start_date"})
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class WeeklyQuest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** 이번 주 시작일 (일요일) */
    @Column(nullable = false)
    private LocalDate weekStartDate;

    /** 이번 주 완료한 일일 퀘스트 수 */
    @Column(nullable = false)
    @Builder.Default
    private int completedCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private WeeklyQuestStatus status = WeeklyQuestStatus.IN_PROGRESS;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * 일일 퀘스트 완료 시 카운트 증가
     * - 7회 달성 시 COMPLETED로 변경
     */
    public void incrementCount() {
        if (this.status != WeeklyQuestStatus.IN_PROGRESS) return;
        this.completedCount++;
        if (this.completedCount >= 7) {
            this.status = WeeklyQuestStatus.COMPLETED;
        }
    }

    /**
     * 보상 지급 완료 처리
     */
    public void reward() {
        if (this.status == WeeklyQuestStatus.COMPLETED) {
            this.status = WeeklyQuestStatus.REWARDED;
        }
    }
}