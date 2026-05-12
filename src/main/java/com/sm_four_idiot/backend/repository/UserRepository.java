package com.sm_four_idiot.backend.repository;

import com.sm_four_idiot.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * 사용자 데이터 접근 레이어
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /** 이메일로 사용자 조회 */
    Optional<User> findByEmail(String email);

    /** 이메일 중복 확인 */
    boolean existsByEmail(String email);

    /**
     * [Backend DB]
     * 흐름: 유저 엔티티를 조회(Select)해서 객체의 xp 필드를 수정한 뒤 저장(Save)하는 대신, 
     * DB에서 직접 "기존 값 + 추가 값" 연산을 수행하는 네이티브 Update 쿼리입니다.
     * flushAutomatically=true 설정으로 쿼리 실행 즉시 변경사항을 동기화합니다.
     */
    //동시성 문제 방지를 위한 경험치 증가 로직
    @Modifying(flushAutomatically = true)
    @Query("UPDATE User u SET u.xp = u.xp + :xpAmount WHERE u.id = :userId")
    void incrementUserXp(@Param("userId") Long userId, @Param("xpAmount") int xpAmount);
}