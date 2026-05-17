package com.sm_four_idiot.backend.controller;

import com.sm_four_idiot.backend.dto.WeeklyQuestResponse;
import com.sm_four_idiot.backend.service.WeeklyQuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 주간 퀘스트 API 컨트롤러
 */
@RestController
@RequestMapping("/api/weekly-quest")
@RequiredArgsConstructor
public class WeeklyQuestController {

    private final WeeklyQuestService weeklyQuestService;

    /**
     * 주간 퀘스트 조회
     * GET /api/weekly-quest
     */
    @GetMapping
    public ResponseEntity<WeeklyQuestResponse> getWeeklyQuest(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(weeklyQuestService.getWeeklyQuest(email));
    }
}