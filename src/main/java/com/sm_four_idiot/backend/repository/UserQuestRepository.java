package com.sm_four_idiot.backend.repository;

import com.sm_four_idiot.backend.domain.User;
import com.sm_four_idiot.backend.domain.UserQuest;
import com.sm_four_idiot.backend.domain.QuestType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserQuestRepository extends JpaRepository<UserQuest, Long> {
    List<UserQuest> findByUser(User user);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT uq FROM UserQuest uq WHERE uq.user = :user AND uq.questType = :questType")
    Optional<UserQuest> findByUserAndQuestTypeWithLock(@Param("user") User user, @Param("questType") QuestType questType);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM UserQuest uq WHERE uq.user = :user AND uq.updatedAt < :startOfToday")
    void deleteOldQuests(@Param("user") User user, @Param("startOfToday") LocalDateTime startOfToday);

    // [Fix - 이미지 4] 비동기 트랜잭션 안전성을 위한 MySQL Native Upsert
    @Modifying
    @Query(value = "INSERT IGNORE INTO user_quest (user_id, quest_type, current_progress, target_progress, status, updated_at) " +
            "VALUES (:userId, :questType, 0, :targetProgress, 'IN_PROGRESS', NOW())", nativeQuery = true)
    void insertIfNotExists(@Param("userId") Long userId, @Param("questType") String questType, @Param("targetProgress") int targetProgress);
}