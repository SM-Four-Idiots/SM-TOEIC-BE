package com.sm_four_idiot.backend.service;

import com.sm_four_idiot.backend.config.JwtTokenProvider;
import com.sm_four_idiot.backend.domain.RefreshToken;
import com.sm_four_idiot.backend.domain.User;
import com.sm_four_idiot.backend.dto.AuthResponse;
import com.sm_four_idiot.backend.dto.LoginRequest;
import com.sm_four_idiot.backend.dto.SignUpRequest;
import com.sm_four_idiot.backend.repository.RefreshTokenRepository;
import com.sm_four_idiot.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
     * - 이메일 중복 확인 후 BCrypt 암호화하여 저장
     * @param request 회원가입 요청 DTO
     * @throws ResponseStatusException 이메일 중복 시 409
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
     * - 이메일/비밀번호 검증 후 액세스 토큰 + 리프레시 토큰 발급
     * @param request 로그인 요청 DTO
     * @return 액세스 토큰 + 리프레시 토큰
     * @throws ResponseStatusException 이메일 없음 또는 비밀번호 불일치 시 401
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다"));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다");
        }

        // 액세스 토큰 발급
        String accessToken = jwtTokenProvider.generateToken(
                user.getEmail(), user.getRole().name());

        // 리프레시 토큰 발급
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);

        // 리프레시 토큰 저장 (기존 토큰 있으면 갱신)
        refreshTokenRepository.findByUser(user)
                .ifPresentOrElse(
                        existing -> existing.updateToken(refreshToken, expiresAt),
                        () -> refreshTokenRepository.save(RefreshToken.builder()
                                .user(user)
                                .token(refreshToken)
                                .expiresAt(expiresAt)
                                .build())
                );

        return new AuthResponse(accessToken, refreshToken);
    }

    /**
     * 토큰 재발급
     * - 리프레시 토큰 검증 후 새 액세스 토큰 + 리프레시 토큰 발급
     * @param refreshToken 리프레시 토큰
     * @return 새 액세스 토큰 + 리프레시 토큰
     * @throws ResponseStatusException 유효하지 않거나 만료된 토큰 시 401
     */
    @Transactional
    public AuthResponse refresh(String refreshToken) {
        // 리프레시 토큰 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다");
        }

        // DB에서 리프레시 토큰 조회
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "존재하지 않는 리프레시 토큰입니다"));

        // 만료 여부 확인
        if (storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "만료된 리프레시 토큰입니다");
        }

        // 새 액세스 토큰 + 리프레시 토큰 발급
        User user = storedToken.getUser();
        String newAccessToken = jwtTokenProvider.generateToken(
                user.getEmail(), user.getRole().name());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());
        storedToken.updateToken(newRefreshToken, LocalDateTime.now().plusDays(7));

        return new AuthResponse(newAccessToken, newRefreshToken);
    }

    /**
     * 로그아웃
     * - DB에서 리프레시 토큰 삭제
     * @param refreshToken 리프레시 토큰
     */
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(refreshTokenRepository::delete);
    }
}