package com.sm_four_idiot.backend.controller;

import com.sm_four_idiot.backend.dto.WordResponse;
import com.sm_four_idiot.backend.service.WordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 단어 관련 API 컨트롤러
 * - 단어 목록 조회, 카테고리별 조회
 */
@RestController
@RequestMapping("/api/words")
@RequiredArgsConstructor
public class WordController {

    private final WordService wordService;

    /**
     * 전체 단어 목록 조회
     * GET /api/words
     */
    @GetMapping
    public ResponseEntity<List<WordResponse>> getAllWords() {
        return ResponseEntity.ok(wordService.getAllWords());
    }

    /**
     * 카테고리별 단어 조회
     * GET /api/words/category?category=business
     */
    @GetMapping("/category")
    public ResponseEntity<List<WordResponse>> getWordsByCategory(
            @RequestParam String category) {
        return ResponseEntity.ok(wordService.getWordsByCategory(category));
    }
}