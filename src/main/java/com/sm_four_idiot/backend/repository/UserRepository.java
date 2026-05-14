package com.sm_four_idiot.backend.repository;

import com.sm_four_idiot.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.Optional;

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
}