package com.sm_four_idiot.backend.repository;

import com.sm_four_idiot.backend.domain.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sm_four_idiot.backend.domain.Tier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 단어 데이터 접근 레이어
 */
public interface WordRepository extends JpaRepository<Word, Long> {

    /** 카테고리별 단어 조회 */
    List<Word> findByCategory(String category);

    List<Word> findByTier(Tier tier);

    /**
     * 단어 길이로 조회
     */
    @Query("SELECT w FROM Word w WHERE LENGTH(w.voca) = :length")
    List<Word> findByVocaLength(@Param("length") int length);
}

