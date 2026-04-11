package com.sm_four_idiot.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 오답 단어 엔티티
 * - 사용자가 테스트에서 틀린 단어를 별도로 저장
 * - 승급 테스트 3회 모두 실패 시 자동 저장 (Sprint 2)
 * - 오답 노트 기능에서 조회
 */
@Entity
@Table(name = "wrong_word")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class WrongWord {

    /** 오답 노트 고유 ID (auto increment) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 오답을 기록한 사용자 (지연 로딩) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** 틀린 단어 (지연 로딩) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    /** 오답 기록 일시 (수정 불가) */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}