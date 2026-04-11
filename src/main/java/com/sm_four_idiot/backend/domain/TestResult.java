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

    /** 테스트 결과 고유 ID (auto increment) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 테스트를 진행한 사용자 (지연 로딩) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** 테스트에 출제된 단어 (지연 로딩) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    /** 정답 여부 (true: 정답, false: 오답) */
    @Column(nullable = false)
    private boolean isCorrect;

    /** 테스트 진행 일시 (수정 불가) */
    @Column(nullable = false, updatable = false)
    private LocalDateTime testedAt;

    @PrePersist
    protected void onCreate() {
        this.testedAt = LocalDateTime.now();
    }
}