package com.sm_four_idiot.backend.repository;

import com.sm_four_idiot.backend.domain.RewardLimit;
import com.sm_four_idiot.backend.domain.RewardType;
import com.sm_four_idiot.backend.domain.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface RewardLimitRepository extends JpaRepository<RewardLimit, Long> {
    Optional<RewardLimit> findByUserAndRewardType(User user, RewardType rewardType);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM RewardLimit r WHERE r.user = :user AND r.rewardType = :rewardType")
    Optional<RewardLimit> findByUserAndRewardTypeWithLock(@Param("user") User user, @Param("rewardType") RewardType rewardType);

    // [Fix - 이미지 3, 4] 트랜잭션 롤백오류(rollback-only) 방지를 위한 MySQL Native Upsert
    @Modifying
    @Query(value = "INSERT IGNORE INTO reward_limit (user_id, reward_type, daily_claimed_amount, last_reward_date) " +
            "VALUES (:userId, :rewardType, 0, :lastRewardDate)", nativeQuery = true)
    void insertIfNotExists(@Param("userId") Long userId, @Param("rewardType") String rewardType, @Param("lastRewardDate") LocalDate lastRewardDate);
}