package com.sm_four_idiot.backend.repository;

import com.sm_four_idiot.backend.domain.User;
import com.sm_four_idiot.backend.domain.Word;
import com.sm_four_idiot.backend.domain.WrongWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 오답 단어 데이터 접근 레이어
 */
public interface WrongWordRepository extends JpaRepository<WrongWord, Long> {

    /** 유저의 오답 노트 전체 조회 (틀린 횟수 내림차순) */
    List<WrongWord> findByUserOrderByWrongCountDesc(User user);

    /** 유저의 특정 단어 오답 조회 */
    Optional<WrongWord> findByUserAndWord(User user, Word word);

    /** 유저의 오답 노트 전체 삭제 */
    @Modifying
    @Query("DELETE FROM WrongWord w WHERE w.user = :user")
    void deleteAllByUser(@Param("user") User user);
}