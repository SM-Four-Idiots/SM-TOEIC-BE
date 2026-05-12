package com.sm_four_idiot.backend.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

/**
 * [DTO] 퀘스트 대시보드 API 전체 응답 객체
 * * [Frontend] /dashboard 호출 시 응답받는 data의 구조입니다.
 * - dailyXp: 퀘스트와 무관하게 사용자가 현재 보유한 총 누적 경험치
 * - quests: 화면에 리스트업해야 할 오늘의 퀘스트 목록
 */
@Getter
@Builder
public class QuestDashboardResponse {
    private final int dailyXp;
    private final List<QuestResponse> quests;
}