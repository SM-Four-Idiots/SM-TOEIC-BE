package com.sm_four_idiot.backend.controller;

import com.sm_four_idiot.backend.dto.QuestDashboardResponse;
import com.sm_four_idiot.backend.dto.ResponseDto;
import com.sm_four_idiot.backend.service.QuestService;
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

    /**
     * [GET] /api/quests/dashboard
     * * [Frontend]
     * 역할: 현재 로그인한 사용자의 일일 퀘스트 진행 상황과 총 경험치(XP)를 조회합니다.
     * 파라미터: 헤더에 포함된 인증 토큰 (별도의 쿼리스트링이나 바디 없음)
     * 반환값: 사용자의 총 XP(dailyXp)와 퀘스트 목록(quests 배열)을 포함한 JSON
     * * [Backend]
     * 흐름: Security Context에서 추출한 userId 문자열을 Long 타입으로 변환하여 Service 계층으로 전달합니다.
     * 잘못된 형식의 userId가 들어올 경우에 대한 기본적인 파싱 방어 로직이 포함되어 있습니다.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<ResponseDto<QuestDashboardResponse>> getQuestDashboard(
            @AuthenticationPrincipal String userId) {
        
        try {
            Long parsedUserId = Long.valueOf(userId);
            QuestDashboardResponse dashboard = questService.getQuestDashboard(parsedUserId);
            return ResponseEntity.ok(ResponseDto.success(dashboard));
        } catch (NumberFormatException e) {
            // userId 파싱 실패에 대한 방어 로직
            throw new IllegalArgumentException("유효하지 않은 사용자 ID 형식입니다.");
        }
    }
}