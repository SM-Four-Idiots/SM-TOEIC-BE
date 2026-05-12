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

/**
 * [Repository] 퀘스트 정보 데이터베이스 접근 객체
 */
public interface UserQuestRepository extends JpaRepository<UserQuest, Long> {
    List<UserQuest> findByUser(User user);

    /**
     * [Backend DB]
     * 흐름: 퀘스트 진행도를 올리기 전, 다른 스레드(요청)가 데이터를 수정하지 못하도록 DB 단에서 배타적 쓰기 락(PESSIMISTIC_WRITE)을 겁니다.
     * 효과: 단어 2개를 동시에 제출하는 등 트래픽이 몰려도 진행도가 유실(Lost Update)되지 않습니다.
     */
    // [Fix 3] 퀘스트 진행도 갱신 손실(Lost Update) 방지를 위한 비관적 락(X-Lock) 적용
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT uq FROM UserQuest uq WHERE uq.user = :user AND uq.questType = :questType")
    Optional<UserQuest> findByUserAndQuestTypeWithLock(@Param("user") User user, @Param("questType") QuestType questType);

    /**
     * [Backend DB]
     * 흐름: JPA 영속성 컨텍스트를 거치지 않고 직접 DB에 DELETE 쿼리를 날려 과거(자정 이전) 데이터를 일괄 삭제합니다.
     * 효과: 데이터를 메모리(App)로 끌고 와서 지우는 N+1 문제와 메모리 낭비를 방지합니다.
     */
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM UserQuest uq WHERE uq.user = :user AND uq.updatedAt < :startOfToday")
    void deleteOldQuests(@Param("user") User user, @Param("startOfToday") LocalDateTime startOfToday);
}