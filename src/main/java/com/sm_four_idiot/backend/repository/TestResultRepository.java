package com.sm_four_idiot.backend.repository;

import com.sm_four_idiot.backend.domain.TestResult;
import com.sm_four_idiot.backend.domain.User;
import com.sm_four_idiot.backend.domain.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TestResultRepository extends JpaRepository<TestResult, Long> {

    // 채점 시 가장 최근 출제 기록 조회
    Optional<TestResult> findTopByUserAndWordOrderByTestedAtDesc(User user, Word word);

    // 집계 시 wordId별 최신 1개씩 조회
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