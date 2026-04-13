package com.sm_four_idiot.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 리프레시 토큰 엔티티
 * - 로그인 시 발급된 리프레시 토큰을 DB에 저장
 * - 액세스 토큰 만료 시 리프레시 토큰으로 재발급
 */
@Entity
@Table(name = "refresh_token")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class RefreshToken {

    /** 리프레시 토큰 고유 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 토큰 소유 사용자 */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** 리프레시 토큰 값 */
    @Column(nullable = false, unique = true)
    private String token;

    /** 토큰 만료 일시 */
    @Column(nullable = false)
    private LocalDateTime expiresAt;

    /**
     * 토큰 값 갱신
     */
    public void updateToken(String token, LocalDateTime expiresAt) {
        this.token = token;
        this.expiresAt = expiresAt;
    }
}