package com.sm_four_idiot.backend.repository;

import com.sm_four_idiot.backend.domain.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 테스트 결과 데이터 접근 레이어
 */
public interface TestResultRepository extends JpaRepository<TestResult, Long> {
}