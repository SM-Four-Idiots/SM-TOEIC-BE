package com.sm_four_idiot.backend.repository;

import com.sm_four_idiot.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE User u SET u.xp = u.xp + :xpAmount WHERE u.id = :userId")
    void incrementUserXp(@Param("userId") Long userId, @Param("xpAmount") int xpAmount);

    // Points ?
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE User u SET u.points = u.points + :pointsAmount WHERE u.id = :userId")
    void incrementUserPoints(@Param("userId") Long userId, @Param("pointsAmount") int pointsAmount);


    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE User u SET u.tierUpgradeEligible = true " +
            "WHERE u.id = :userId AND u.xp >= :requiredXp")
    void updateTierUpgradeEligibleIfReached(
            @Param("userId") Long userId,
            @Param("requiredXp") int requiredXp);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE User u SET u.currentStreak = :currentStreak, u.maxStreak = :maxStreak, " +
            "u.lastLearnedDate = :lastLearnedDate WHERE u.id = :userId")
    void updateStreak(@Param("userId") Long userId,
                      @Param("currentStreak") int currentStreak,
                      @Param("maxStreak") int maxStreak,
                      @Param("lastLearnedDate") LocalDate lastLearnedDate);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE User u SET u.totalDailyQuestCompleted = u.totalDailyQuestCompleted + 1 WHERE u.id = :userId")
    void incrementTotalDailyQuestCompleted(@Param("userId") Long userId);

    /**
     * 티어 내림차순, 동일 티어 내 XP 내림차순으로 전체 유저 조회
     * - DIAMOND(5) > PLATINUM(4) > GOLD(3) > SILVER(2) > BRONZE(1) 순으로 정렬
     */
    @Query("SELECT u FROM User u ORDER BY " +
            "CASE u.tier " +
            "WHEN 'DIAMOND' THEN 5 " +
            "WHEN 'PLATINUM' THEN 4 " +
            "WHEN 'GOLD' THEN 3 " +
            "WHEN 'SILVER' THEN 2 " +
            "WHEN 'BRONZE' THEN 1 END DESC, " +
            "u.xp DESC")
    List<User> findAllOrderByTierAndXp();

    /**
     * 워들 플레이 날짜 업데이트
     */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE User u SET u.lastWordleDate = :date WHERE u.id = :userId")
    void updateLastWordleDate(@Param("userId") Long userId, @Param("date") LocalDate date);
}