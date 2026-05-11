package com.sm_four_idiot.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 일일 퀘스트 완료 기록
 * - 하루 최대 3회 제한 (TierConfig.DAILY_QUEST_LIMIT)
 */
@Entity
@Table(name = "daily_quest_record",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "quest_date"})
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class DailyQuestRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** 퀘스트 진행 날짜 */
    @Column(nullable = false)
    private LocalDate questDate;

    /** 오늘 완료한 퀘스트 횟수 */
    @Column(nullable = false)
    @Builder.Default
    private int completedCount = 0;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void incrementCount() {
        this.completedCount++;
    }
}