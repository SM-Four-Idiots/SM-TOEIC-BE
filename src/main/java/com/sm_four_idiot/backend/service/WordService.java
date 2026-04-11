package com.sm_four_idiot.backend.service;

import com.sm_four_idiot.backend.dto.WordResponse;
import com.sm_four_idiot.backend.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}