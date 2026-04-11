package com.sm_four_idiot.backend.repository;

import com.sm_four_idiot.backend.domain.Word;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 단어 데이터 접근 레이어
 */
public interface WordRepository extends JpaRepository<Word, Long> {

    /** 카테고리별 단어 조회 */
    List<Word> findByCategory(String category);
}