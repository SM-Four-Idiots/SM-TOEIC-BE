package com.sm_four_idiot.backend.service;

import com.sm_four_idiot.backend.config.JwtTokenProvider;
import com.sm_four_idiot.backend.domain.RefreshToken;
import com.sm_four_idiot.backend.domain.User;
import com.sm_four_idiot.backend.dto.AuthResponse;
import com.sm_four_idiot.backend.dto.LoginRequest;
import com.sm_four_idiot.backend.dto.SignUpRequest;
import com.sm_four_idiot.backend.repository.RefreshTokenRepository;
import com.sm_four_idiot.backend.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 인증 관련 비즈니스 로직
 * - 회원가입, 로그인, 로그아웃, 토큰 재발급 처리
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 회원가입
     */
    @Transactional
    public void signUp(SignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다");
        }
        try {
            User user = User.builder()
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .nickname(request.getNickname())
                    .role(User.Role.USER)
                    .build();
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다");
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "회원가입 중 오류가 발생했습니다");
        }
    }

    /**
     * 로그인
     * - accessToken JSON 반환
     * - refreshToken HttpOnly 쿠키로 설정
     */
    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletResponse response) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다");
        }

        String accessToken = jwtTokenProvider.generateToken(
                user.getEmail(), user.getRole().name());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);

        refreshTokenRepository.findByUser(user)
                .ifPresentOrElse(
                        existing -> existing.updateToken(refreshToken, expiresAt),
                        () -> refreshTokenRepository.save(RefreshToken.builder()
                                .user(user)
                                .token(refreshToken)
                                .expiresAt(expiresAt)
                                .build())
                );

        setRefreshTokenCookie(response, refreshToken);

        return new AuthResponse(accessToken);
    }

    /**
     * 토큰 재발급
     * - 쿠키의 refreshToken 검증 후 새 accessToken + refreshToken 발급
     * - 새 refreshToken도 쿠키로 갱신
     */
    @Transactional
    public AuthResponse refresh(String refreshToken, HttpServletResponse response) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다");
        }

        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "존재하지 않는 리프레시 토큰입니다"));

        if (storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "만료된 리프레시 토큰입니다");
        }

        User user = storedToken.getUser();
        String newAccessToken = jwtTokenProvider.generateToken(
                user.getEmail(), user.getRole().name());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());
        storedToken.updateToken(newRefreshToken, LocalDateTime.now().plusDays(7));

        setRefreshTokenCookie(response, newRefreshToken);

        return new AuthResponse(newAccessToken);
    }

    /**
     * 로그아웃
     * - DB에서 리프레시 토큰 삭제
     * - 쿠키 만료 처리
     */
    @Transactional
    public void logout(String refreshToken, HttpServletResponse response) {
        refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(refreshTokenRepository::delete);

        // 쿠키 만료
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/api/auth/refresh")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    /**
     * refreshToken HttpOnly 쿠키 설정 공통 메서드
     */
    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)      // HTTPS 적용 시 true로 변경
                .path("/api/auth/refresh")
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}