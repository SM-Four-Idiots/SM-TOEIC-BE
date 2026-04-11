package com.sm_four_idiot.backend.service;

import com.sm_four_idiot.backend.dto.WordResponse;
import com.sm_four_idiot.backend.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sm_four_idiot.backend.domain.Word;
import com.sm_four_idiot.backend.dto.WordRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 단어 관련 비즈니스 로직
 */
@Service
@RequiredArgsConstructor
public class WordService {

    private final WordRepository wordRepository;

    /**
     * 전체 단어 목록 조회
     *
     * @return 전체 단어 리스트
     */
    @Transactional(readOnly = true)
    public List<WordResponse> getAllWords() {
        return wordRepository.findAll()
                .stream()
                .map(WordResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 카테고리별 단어 조회
     *
     * @param category 카테고리명 (business, economy, travel 등)
     * @return 해당 카테고리 단어 리스트
     */
    @Transactional(readOnly = true)
    public List<WordResponse> getWordsByCategory(String category) {
        return wordRepository.findByCategory(category)
                .stream()
                .map(WordResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 단어 추가 (관리자)
     *
     * @param request 단어 추가 요청 DTO
     */
    @Transactional
    public WordResponse createWord(WordRequest request) {
        Word word = Word.builder()
                .english(request.getEnglish())
                .meaning(request.getMeaning())
                .category(request.getCategory())
                .tierLevel(request.getTierLevel())
                .build();
        return new WordResponse(wordRepository.save(word));
    }

    /**
     * 단어 수정 (관리자)
     *
     * @param id      수정할 단어 ID
     * @param request 단어 수정 요청 DTO
     */
    @Transactional
    public WordResponse updateWord(Long id, WordRequest request) {
        Word word = wordRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "단어를 찾을 수 없습니다"));
        word.update(request.getEnglish(), request.getMeaning(),
                request.getCategory(), request.getTierLevel());
        return new WordResponse(word);
    }

    /**
     * 단어 삭제 (관리자)
     *
     * @param id 삭제할 단어 ID
     */
    @Transactional
    public void deleteWord(Long id) {
        Word word = wordRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "단어를 찾을 수 없습니다"));
        wordRepository.delete(word);
    }

}