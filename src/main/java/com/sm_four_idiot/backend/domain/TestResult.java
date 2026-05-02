package com.sm_four_idiot.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 단어 테스트 결과 엔티티
 * - 사용자가 단어 테스트를 진행할 때마다 결과를 저장
 * - 오답 단어는 WrongWord에 별도 저장
 */
@Entity
@Table(name = "test_result")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class TestResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    @Column(nullable = false)
    private boolean isCorrect;

    /** 문제 유형 (0: 영단어 맞히기, 1: 한글 뜻 맞히기) */
    @Column(nullable = false)
    private int type;

    @Column(nullable = false, updatable = false)
    private LocalDateTime testedAt;

    @PrePersist
    protected void onCreate() {
        this.testedAt = LocalDateTime.now();
    }

    public void updateResult(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }
}