package com.sm_four_idiot.backend.service;

import com.sm_four_idiot.backend.config.JwtTokenProvider;
import com.sm_four_idiot.backend.domain.User;
import com.sm_four_idiot.backend.dto.AuthResponse;
import com.sm_four_idiot.backend.dto.LoginRequest;
import com.sm_four_idiot.backend.dto.SignUpRequest;
import com.sm_four_idiot.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증 관련 비즈니스 로직
 * - 회원가입, 로그인, 로그아웃 처리
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 회원가입
     * - 이메일 중복 확인 후 BCrypt 암호화하여 저장
     * @param request 회원가입 요청 DTO
     */
    @Transactional
    public void signUp(SignUpRequest request) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .role(User.Role.USER)
                .build();

        userRepository.save(user);
    }

    /**
     * 로그인
     * - 이메일/비밀번호 검증 후 JWT 토큰 발급
     * @param request 로그인 요청 DTO
     * @return JWT 액세스 토큰
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다"));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다");
        }

        // JWT 토큰 발급
        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token);
    }

    /**
     * 로그아웃
     * - JWT는 서버에 상태를 저장하지 않으므로 클라이언트에서 토큰 삭제
     * - 추후 Redis 블랙리스트 방식으로 고도화 가능
     */
    public void logout() {
        // 클라이언트에서 토큰 삭제로 처리
        // Sprint 2에서 Redis 블랙리스트 방식 도입 예정
    }
}