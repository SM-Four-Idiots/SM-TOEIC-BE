package com.sm_four_idiot.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 오답 단어 엔티티
 * - 단어별 틀린 횟수 누적
 * - 학습 완료 시 삭제
 */
@Entity
@Table(name = "wrong_word",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "word_id"})
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class WrongWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    /** 틀린 횟수 */
    @Column(nullable = false)
    @Builder.Default
    private int wrongCount = 1;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * 틀린 횟수 증가
     */
    public void incrementWrongCount() {
        this.wrongCount++;
    }
}