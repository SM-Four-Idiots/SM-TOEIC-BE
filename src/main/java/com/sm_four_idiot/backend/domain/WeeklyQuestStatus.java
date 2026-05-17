package com.sm_four_idiot.backend.domain;

/**
 * 주간 퀘스트 상태
 */
public enum WeeklyQuestStatus {
    IN_PROGRESS, // 진행 중
    COMPLETED,   // 목표 달성 (보상 대기)
    REWARDED     // 보상 지급 완료
}