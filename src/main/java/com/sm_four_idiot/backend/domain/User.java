package com.sm_four_idiot.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 사용자 엔티티
 * - 일반 사용자(USER)와 관리자(ADMIN) 권한을 구분
 * - 이메일을 고유 식별자로 사용
 */

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class User {

    /** 사용자 고유 ID (auto increment) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 로그인 이메일 (중복 불가) */
    @Column(nullable = false, unique = true)
    private String email;

    /** BCrypt 암호화된 비밀번호 */
    @Column(nullable = false)
    private String password;

    /** 사용자 닉네임 */
    @Column(nullable = false)
    private String nickname;

    /** 권한: USER(일반 사용자), ADMIN(관리자) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /** 계정 생성 일시 (수정 불가) */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public enum Role {
        USER, ADMIN
    }

}