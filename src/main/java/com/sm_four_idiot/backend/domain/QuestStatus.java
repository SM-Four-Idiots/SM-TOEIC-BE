package com.sm_four_idiot.backend.domain;

/**
 * [Domain Enum] 퀘스트의 현재 진행 상태를 나타내는 열거형
 * * [Frontend] UI 렌더링 시 진행 중, 완료(보상 대기), 보상 수령 완료 상태를 구분하는 데 사용됩니다.
 */
public enum QuestStatus {
    IN_PROGRESS, // 진행 중
    COMPLETED,   // 목표 달성 (보상 지급 전처리 상태)
    REWARDED     // 보상 지급 완료 (최종 상태)
}