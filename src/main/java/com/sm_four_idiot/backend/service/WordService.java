package com.sm_four_idiot.backend.service;

import com.sm_four_idiot.backend.domain.Tier;
import com.sm_four_idiot.backend.domain.User;
import com.sm_four_idiot.backend.domain.Word;
import com.sm_four_idiot.backend.dto.WordRequest;
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
 * 단어 관련 비즈니스 로직
 */
@Service
@RequiredArgsConstructor
public class WordService {

    private final WordRepository wordRepository;
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;

    /**
     * 전체 단어 목록 조회
     * @param email JWT에서 추출한 이메일
     * @return 전체 단어 리스트 (북마크 여부 포함)
     */
    @Transactional(readOnly = true)
    public List<WordResponse> getAllWords(String email) {
        User user = getUser(email);
        Set<Long> bookmarkedIds = bookmarkRepository.findBookmarkedWordIds(user);
        return wordRepository.findAll()
                .stream()
                .map(word -> new WordResponse(word, bookmarkedIds.contains(word.getId())))
                .collect(Collectors.toList());
    }

    /**
     * 카테고리별 단어 조회
     * @param email JWT에서 추출한 이메일
     * @param category 카테고리명
     * @return 해당 카테고리 단어 리스트 (북마크 여부 포함)
     */
    @Transactional(readOnly = true)
    public List<WordResponse> getWordsByCategory(String email, String category) {
        User user = getUser(email);
        Set<Long> bookmarkedIds = bookmarkRepository.findBookmarkedWordIds(user);
        return wordRepository.findByCategory(category)
                .stream()
                .map(word -> new WordResponse(word, bookmarkedIds.contains(word.getId())))
                .collect(Collectors.toList());
    }

    /**
     * 티어별 단어 조회
     * @param email JWT에서 추출한 이메일
     * @param tier 티어
     * @return 해당 티어 단어 리스트 (북마크 여부 포함)
     */
    @Transactional(readOnly = true)
    public List<WordResponse> getWordsByTier(String email, Tier tier) {
        User user = getUser(email);
        Set<Long> bookmarkedIds = bookmarkRepository.findBookmarkedWordIds(user);
        return wordRepository.findByTier(tier)
                .stream()
                .map(word -> new WordResponse(word, bookmarkedIds.contains(word.getId())))
                .collect(Collectors.toList());
    }

    /**
     * 단어 검색
     * @param email JWT에서 추출한 이메일
     * @param keyword 검색어
     * @param tier 티어 필터 (없으면 null)
     * @return 검색 결과 단어 리스트 (북마크 여부 포함)
     */
    @Transactional(readOnly = true)
    public List<WordResponse> searchWords(String email, String keyword, Tier tier) {
        User user = getUser(email);
        Set<Long> bookmarkedIds = bookmarkRepository.findBookmarkedWordIds(user);
        return wordRepository.searchWords(keyword, tier)
                .stream()
                .map(word -> new WordResponse(word, bookmarkedIds.contains(word.getId())))
                .collect(Collectors.toList());
    }

    /**
     * 단어 추가 (관리자)
     * @param request 단어 추가 요청 DTO
     */
    @Transactional
    public WordResponse createWord(WordRequest request) {
        Word word = Word.builder()
                .voca(request.getVoca().trim())
                .meaning(request.getMeaning().trim())
                .category(request.getCategory().trim())
                .tier(request.getTier())
                .build();
        return new WordResponse(wordRepository.save(word));
    }

    /**
     * 단어 수정 (관리자)
     * @param id 수정할 단어 ID
     * @param request 단어 수정 요청 DTO
     */
    @Transactional
    public WordResponse updateWord(Long id, WordRequest request) {
        Word word = wordRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "단어를 찾을 수 없습니다"));
        word.update(request.getVoca(), request.getMeaning(),
                request.getCategory(), request.getTier());
        return new WordResponse(word);
    }

    /**
     * 단어 삭제 (관리자)
     * @param id 삭제할 단어 ID
     */
    @Transactional
    public void deleteWord(Long id) {
        Word word = wordRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "단어를 찾을 수 없습니다"));
        wordRepository.delete(word);
    }

    /**
     * 이메일로 유저 조회 헬퍼 메서드
     */
    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다"));
    }
}