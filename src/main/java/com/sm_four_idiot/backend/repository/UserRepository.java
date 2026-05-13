package com.sm_four_idiot.backend.repository;

import com.sm_four_idiot.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE User u SET u.xp = u.xp + :xpAmount WHERE u.id = :userId")
    void incrementUserXp(@Param("userId") Long userId, @Param("xpAmount") int xpAmount);

    // [Fix - 이미지 2] OSIV 의존성 탈피 및 포인트 증발 방지를 위한 직접 Update 쿼리
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE User u SET u.points = u.points + :pointsAmount WHERE u.id = :userId")
    void incrementUserPoints(@Param("userId") Long userId, @Param("pointsAmount") int pointsAmount);
}