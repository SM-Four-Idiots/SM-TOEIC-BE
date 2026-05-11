package com.sm_four_idiot.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * XP 획득 이력 (PBI-6.2)
 * - 마이페이지에서 최근 XP 획득 내역 표시용
 */
@Entity
@Table(name = "xp_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class XpHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** 획득한 XP (양수만 허용) */
    @Column(nullable = false)
    private int xpGained;

    /** 획득 사유 (빈 문자열 불가) */
    @Column(nullable = false)
    private String reason;

    @Column(nullable = false, updatable = false)
    private LocalDateTime earnedAt;

    @PrePersist
    protected void onCreate() {
        this.earnedAt = LocalDateTime.now();
    }

    /** 검증 후 생성 */
    public static XpHistory of(User user, int xpGained, String reason) {
        if (xpGained <= 0) throw new IllegalArgumentException("XP는 양수여야 합니다");
        if (reason == null || reason.isBlank()) throw new IllegalArgumentException("reason은 필수입니다");
        return XpHistory.builder()
                .user(user)
                .xpGained(xpGained)
                .reason(reason)
                .build();
    }
}