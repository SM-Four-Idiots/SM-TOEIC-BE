package com.sm_four_idiot.backend.repository;

import com.sm_four_idiot.backend.domain.RefreshToken;
import com.sm_four_idiot.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 리프레시 토큰 데이터 접근 레이어
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /** 토큰 값으로 조회 */
    Optional<RefreshToken> findByToken(String token);

    /** 사용자로 조회 */
    Optional<RefreshToken> findByUser(User user);
}