package com.sm_four_idiot.backend.controller;

import com.sm_four_idiot.backend.dto.TierUpgradeQuestionResponse;
import com.sm_four_idiot.backend.dto.TierUpgradeResultRequest;
import com.sm_four_idiot.backend.dto.TierUpgradeResultResponse;
import com.sm_four_idiot.backend.service.TierUpgradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 티어 승급 테스트 API 컨트롤러
 */
@RestController
@RequestMapping("/api/tier-upgrade")
@RequiredArgsConstructor
public class TierUpgradeController {

    private final TierUpgradeService tierUpgradeService;

    /**
     * 승급 테스트 문제 출제
     * GET /api/tier-upgrade/questions
     */
    @GetMapping("/questions")
    public ResponseEntity<List<TierUpgradeQuestionResponse>> getQuestions(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(tierUpgradeService.getQuestions(email));
    }

    /**
     * 승급 테스트 답안 제출
     * POST /api/tier-upgrade/result
     */
    @PostMapping("/result")
    public ResponseEntity<TierUpgradeResultResponse> submitResult(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody TierUpgradeResultRequest request) {
        return ResponseEntity.ok(tierUpgradeService.submitResult(email, request));
    }
}