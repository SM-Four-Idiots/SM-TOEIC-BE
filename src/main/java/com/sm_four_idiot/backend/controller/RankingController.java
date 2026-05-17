package com.sm_four_idiot.backend.controller;

import com.sm_four_idiot.backend.dto.RankingResponse;
import com.sm_four_idiot.backend.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 랭킹 보드 API 컨트롤러
 */
@RestController
@RequestMapping("/api/ranking")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    /**
     * 전체 랭킹 조회
     * GET /api/ranking
     */
    @GetMapping
    public ResponseEntity<RankingResponse> getRanking(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(rankingService.getRanking(email));
    }
}