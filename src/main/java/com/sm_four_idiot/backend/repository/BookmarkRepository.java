package com.sm_four_idiot.backend.repository;

import com.sm_four_idiot.backend.domain.Bookmark;
import com.sm_four_idiot.backend.domain.User;
import com.sm_four_idiot.backend.domain.Word;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 북마크 데이터 접근 레이어
 */
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    /** 유저의 북마크 전체 조회 */
    List<Bookmark> findByUser(User user);

    /** 유저의 특정 단어 북마크 조회 */
    Optional<Bookmark> findByUserAndWord(User user, Word word);

    /** 유저의 북마크 단어 ID 목록 조회 */
    default Set<Long> findBookmarkedWordIds(User user) {
        return findByUser(user).stream()
                .map(bookmark -> bookmark.getWord().getId())
                .collect(Collectors.toSet());
    }
}