package com.sm_four_idiot.backend.dto;

import com.sm_four_idiot.backend.domain.WeeklyQuestStatus;
import lombok.Builder;
import lombok.Getter;

/**
 * 주간 퀘스트 응답 DTO
 */
@Getter
@Builder
public class WeeklyQuestResponse {
    /** 이번 주 완료한 일일 퀘스트 수 */
    private int completedCount;
    /** 목표 횟수 (고정 7) */
    private int targetCount;
    /** 주간 퀘스트 상태 */
    private WeeklyQuestStatus status;
    /** 보상 XP */
    private int rewardXp;
}