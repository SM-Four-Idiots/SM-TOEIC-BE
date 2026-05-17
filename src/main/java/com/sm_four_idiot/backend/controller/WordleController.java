package com.sm_four_idiot.backend.controller;

import com.sm_four_idiot.backend.dto.WordleStartResponse;
import com.sm_four_idiot.backend.service.WordleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 워들 게임 API 컨트롤러
 */
@RestController
@RequestMapping("/api/wordle")
@RequiredArgsConstructor
public class WordleController {

    private final WordleService wordleService;

    /**
     * 워들 게임 시작
     * GET /api/wordle/start
     */
    @GetMapping("/start")
    public ResponseEntity<WordleStartResponse> startGame(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(wordleService.startGame(email));
    }

    /**
     * 워들 게임 완료 처리
     * POST /api/wordle/complete
     */
    @PostMapping("/complete")
    public ResponseEntity<Void> completeGame(
            @AuthenticationPrincipal String email) {
        wordleService.completeGame(email);
        return ResponseEntity.ok().build();
    }
}