package com.sm_four_idiot.backend.domain;

import com.sm_four_idiot.backend.config.TierConfig;
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

    /** 동시 요청 충돌 방지 */
    @Version
    private Long version;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /** 상한선 체크 후 카운트 증가 */
    public void incrementCount() {
        if (this.completedCount >= TierConfig.DAILY_QUEST_LIMIT) {
            throw new IllegalStateException("일일 퀘스트 상한에 도달했습니다");
        }
        this.completedCount++;
    }
}