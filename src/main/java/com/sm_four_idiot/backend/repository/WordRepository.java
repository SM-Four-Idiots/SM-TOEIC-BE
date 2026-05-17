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

    /**
     * 영단어 또는 한글 뜻으로 검색 + 티어 필터
     * - tier가 null이면 전체 조회
     */
    @Query("SELECT w FROM Word w WHERE " +
            "(w.voca LIKE %:keyword% OR w.meaning LIKE %:keyword%) " +
            "AND (:tier IS NULL OR w.tier = :tier)")
    List<Word> searchWords(@Param("keyword") String keyword, @Param("tier") Tier tier);
}

