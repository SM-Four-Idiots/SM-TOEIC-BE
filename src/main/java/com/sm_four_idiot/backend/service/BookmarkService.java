package com.sm_four_idiot.backend.service;

import com.sm_four_idiot.backend.domain.Bookmark;
import com.sm_four_idiot.backend.domain.User;
import com.sm_four_idiot.backend.domain.Word;
import com.sm_four_idiot.backend.dto.WordResponse;
import com.sm_four_idiot.backend.repository.BookmarkRepository;
import com.sm_four_idiot.backend.repository.UserRepository;
import com.sm_four_idiot.backend.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 북마크 비즈니스 로직
 */
@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final WordRepository wordRepository;

    /**
     * 북마크 추가
     * @param email JWT에서 추출한 이메일
     * @param wordId 북마크할 단어 ID
     */
    @Transactional
    public void addBookmark(String email, Long wordId) {
        User user = getUser(email);
        Word word = getWord(wordId);

        // 이미 북마크된 경우 무시
        bookmarkRepository.findByUserAndWord(user, word)
                .ifPresent(b -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT, "이미 북마크된 단어입니다");
                });

        bookmarkRepository.save(Bookmark.builder()
                .user(user)
                .word(word)
                .build());
    }

    /**
     * 북마크 삭제
     * @param email JWT에서 추출한 이메일
     * @param wordId 북마크 삭제할 단어 ID
     */
    @Transactional
    public void removeBookmark(String email, Long wordId) {
        User user = getUser(email);
        Word word = getWord(wordId);

        Bookmark bookmark = bookmarkRepository.findByUserAndWord(user, word)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "북마크된 단어가 아닙니다"));

        bookmarkRepository.delete(bookmark);
    }

    /**
     * 북마크 목록 조회
     * @param email JWT에서 추출한 이메일
     * @return 북마크된 단어 목록
     */
    @Transactional(readOnly = true)
    public List<WordResponse> getBookmarks(String email) {
        User user = getUser(email);
        Set<Long> bookmarkedIds = bookmarkRepository.findBookmarkedWordIds(user);
        return bookmarkRepository.findByUser(user)
                .stream()
                .map(bookmark -> new WordResponse(bookmark.getWord(), true))
                .collect(Collectors.toList());
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다"));
    }

    private Word getWord(Long wordId) {
        return wordRepository.findById(wordId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "단어를 찾을 수 없습니다"));
    }
}