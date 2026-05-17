package com.sm_four_idiot.backend.controller;

import com.sm_four_idiot.backend.dto.WrongWordListResponse;
import com.sm_four_idiot.backend.service.WrongWordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 오답 노트 API 컨트롤러
 */
@RestController
@RequestMapping("/api/wrong-words")
@RequiredArgsConstructor
public class WrongWordController {

    private final WrongWordService wrongWordService;

    /**
     * 오답 노트 목록 조회
     * GET /api/wrong-words
     */
    @GetMapping
    public ResponseEntity<WrongWordListResponse> getWrongWords(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(wrongWordService.getWrongWords(email));
    }

    /**
     * 오답 노트 전체 삭제
     * DELETE /api/wrong-words
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteAllWrongWords(
            @AuthenticationPrincipal String email) {
        wrongWordService.deleteAllWrongWords(email);
        return ResponseEntity.noContent().build();
    }

    /**
     * 오답 노트 단어 하나 삭제 (학습 완료)
     * DELETE /api/wrong-words/{wordId}
     */
    @DeleteMapping("/{wordId}")
    public ResponseEntity<Void> deleteWrongWord(
            @AuthenticationPrincipal String email,
            @PathVariable Long wordId) {
        wrongWordService.deleteWrongWord(email, wordId);
        return ResponseEntity.noContent().build();
    }
}