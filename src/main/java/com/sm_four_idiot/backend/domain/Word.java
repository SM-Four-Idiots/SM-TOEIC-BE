package com.sm_four_idiot.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * 토익 단어 엔티티
 * - 관리자가 웹 대시보드에서 CRUD 가능
 * - tier_level은 Sprint 2에서 Tier 엔티티와 연관관계 추가 예정
 */
@Entity
@Table(name = "word", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"english", "category"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Word {

    /** 단어 고유 ID (auto increment) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 영어 단어 */
    @Column(nullable = false)
    private String english;

    /** 한글 뜻 */
    @Column(nullable = false)
    private String meaning;

    /** 단어 카테고리 (예: business, daily, academic 등) */
    @Column
    private String category;

    /** 난이도 티어 (1~5, Sprint 2에서 Tier 엔티티와 연동 예정) */
    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private int tierLevel;

    /** 단어 등록 일시 (수정 불가) */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}