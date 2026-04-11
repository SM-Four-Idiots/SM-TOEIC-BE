package com.sm_four_idiot.backend.controller;

import com.sm_four_idiot.backend.dto.WordRequest;
import com.sm_four_idiot.backend.dto.WordResponse;
import com.sm_four_idiot.backend.service.WordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 관리자 단어 CRUD API 컨트롤러
 * - ADMIN 권한만 접근 가능
 */
@RestController
@RequestMapping("/api/admin/words")
@RequiredArgsConstructor
public class AdminWordController {

    private final WordService wordService;

    /**
     * 단어 추가
     * POST /api/admin/words
     */
    @PostMapping
    public ResponseEntity<WordResponse> createWord(@Valid @RequestBody WordRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(wordService.createWord(request));
    }

    /**
     * 단어 수정
     * PUT /api/admin/words/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<WordResponse> updateWord(@PathVariable Long id,
                                                   @Valid @RequestBody WordRequest request) {
        return ResponseEntity.ok(wordService.updateWord(id, request));
    }

    /**
     * 단어 삭제
     * DELETE /api/admin/words/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWord(@PathVariable Long id) {
        wordService.deleteWord(id);
        return ResponseEntity.noContent().build();
    }
}