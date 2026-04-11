package com.sm_four_idiot.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 로그인 성공 응답 DTO
 * - JWT 액세스 토큰 반환
 */
@Getter
@AllArgsConstructor
public class AuthResponse {

    /** 발급된 JWT 액세스 토큰 */
    private String accessToken;

    /** 토큰 타입 (Bearer 고정) */
    private String tokenType = "Bearer";

    public AuthResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}