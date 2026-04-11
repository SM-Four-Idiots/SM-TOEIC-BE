package com.sm_four_idiot.backend.repository;

import com.sm_four_idiot.backend.domain.WrongWord;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 오답 단어 데이터 접근 레이어
 */
public interface WrongWordRepository extends JpaRepository<WrongWord, Long> {
}