package com.sm_four_idiot.backend.controller;

import com.sm_four_idiot.backend.domain.User;
import com.sm_four_idiot.backend.dto.QuestDashboardResponse;
import com.sm_four_idiot.backend.dto.ResponseDto;
import com.sm_four_idiot.backend.service.QuestService;
import com.sm_four_idiot.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * [API Controller] 퀘스트 관련 클라이언트 요청을 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/api/quests")
@RequiredArgsConstructor
public class QuestController {

    private final QuestService questService;
    private final UserService userService;

    /**
     * [GET] /api/quests/dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<ResponseDto<QuestDashboardResponse>> getQuestDashboard(
            @AuthenticationPrincipal String email) {

        // 1. 이메일을 기반으로 User 엔티티 조회
        User user = userService.findByEmail(email);

        // 2. 유저의 ID를 서비스로 전달
        QuestDashboardResponse dashboard = questService.getQuestDashboard(user.getId());

        return ResponseEntity.ok(ResponseDto.success(dashboard));
    }
}