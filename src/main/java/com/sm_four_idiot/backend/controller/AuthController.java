package com.sm_four_idiot.backend.controller;

import com.sm_four_idiot.backend.dto.AuthResponse;
import com.sm_four_idiot.backend.dto.LoginRequest;
import com.sm_four_idiot.backend.dto.SignUpRequest;
import com.sm_four_idiot.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.CookieValue;

/**
 * 인증 관련 API 컨트롤러
 * - 회원가입, 로그인, 로그아웃, 토큰 재발급
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입
     * POST /api/auth/signup
     */
    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@Valid @RequestBody SignUpRequest request) {
        authService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 로그인
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        return ResponseEntity.ok(authService.login(request, response));
    }

    /**
     * 토큰 재발급
     * POST /api/auth/refresh
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @CookieValue("refreshToken") String refreshToken, HttpServletResponse response) {
        return ResponseEntity.ok(authService.refresh(refreshToken,  response));
    }

    /**
     * 로그아웃
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {
        authService.logout(refreshToken, response);
        return ResponseEntity.ok().build();
    }
}