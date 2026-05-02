package com.sm_four_idiot.backend.repository;

import com.sm_four_idiot.backend.domain.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sm_four_idiot.backend.domain.TestResult;
import com.sm_four_idiot.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sm_four_idiot.backend.domain.TestResult;
import com.sm_four_idiot.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 테스트 결과 데이터 접근 레이어
 */
public interface TestResultRepository extends JpaRepository<TestResult, Long> {

    List<TestResult> findByUserAndWordIdIn(User user, List<Long> wordIds);

    @Query("""
        SELECT t FROM TestResult t
        WHERE t.user = :user
        AND t.word.id IN :wordIds
        AND t.testedAt = (
            SELECT MAX(t2.testedAt) FROM TestResult t2
            WHERE t2.user = :user AND t2.word.id = t.word.id
        )
    """)
    List<TestResult> findLatestByUserAndWordIdIn(
            @Param("user") User user,
            @Param("wordIds") List<Long> wordIds
    );
}