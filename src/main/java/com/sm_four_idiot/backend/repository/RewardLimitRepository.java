package com.sm_four_idiot.backend.repository;

import com.sm_four_idiot.backend.domain.RewardLimit;
import com.sm_four_idiot.backend.domain.RewardType;
import com.sm_four_idiot.backend.domain.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * [Back-end] reward_limit 테이블에 직접 접근하여 데이터를 조작하는 Repository입니다.
 */
@Repository
public interface RewardLimitRepository extends JpaRepository<RewardLimit, Long> {
    
    /**
     * [Back-end] 상태 조회용 메서드
     * DB 흐름: 단순 SELECT 쿼리를 발생시킵니다. 동시성 문제가 없는 단순 화면 렌더링용 데이터 추출에 쓰입니다.
     */
    Optional<RewardLimit> findByUserAndRewardType(User user, RewardType rewardType);

    /**
     * [Back-end] 보상 획득용(동시성 제어) 메서드
     * DB 흐름: 클라이언트의 따닥(다중 클릭) 방지를 위해 SELECT ... FOR UPDATE 쿼리를 발생시킵니다.
     * 먼저 접근한 트랜잭션이 UPDATE를 마치고 커밋할 때까지, 다른 요청은 데이터베이스 Row 레벨에서 대기하게 됩니다.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM RewardLimit r WHERE r.user = :user AND r.rewardType = :rewardType")
    Optional<RewardLimit> findByUserAndRewardTypeWithLock(@Param("user") User user, @Param("rewardType") RewardType rewardType);
}