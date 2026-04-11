package com.sm_four_idiot.backend.repository;

import com.sm_four_idiot.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 사용자 데이터 접근 레이어
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /** 이메일로 사용자 조회 */
    Optional<User> findByEmail(String email);

    /** 이메일 중복 확인 */
    boolean existsByEmail(String email);
}