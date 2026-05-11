package com.sm_four_idiot.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 로그인/토큰 재발급 응답 DTO
 * - accessToken만 반환 (refreshToken은 HttpOnly 쿠키로 전달)
 */
@Getter
@AllArgsConstructor
public class AuthResponse {

    /** 액세스 토큰 */
    private String accessToken;
}