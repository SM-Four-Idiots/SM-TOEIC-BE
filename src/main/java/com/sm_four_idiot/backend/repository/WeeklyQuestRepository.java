package com.sm_four_idiot.backend.repository;

import com.sm_four_idiot.backend.domain.User;
import com.sm_four_idiot.backend.domain.WeeklyQuest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface WeeklyQuestRepository extends JpaRepository<WeeklyQuest, Long> {

    /**
     * 유저의 특정 주 퀘스트 조회
     */
    Optional<WeeklyQuest> findByUserAndWeekStartDate(User user, LocalDate weekStartDate);

    /**
     * 동시 요청 방어를 위한 INSERT IGNORE
     */
    @Modifying
    @Query(value = "INSERT IGNORE INTO weekly_quest (user_id, week_start_date, completed_count, status, created_at) " +
            "VALUES (:userId, :weekStartDate, 0, 'IN_PROGRESS', NOW())", nativeQuery = true)
    void insertIfNotExists(@Param("userId") Long userId, @Param("weekStartDate") LocalDate weekStartDate);
}