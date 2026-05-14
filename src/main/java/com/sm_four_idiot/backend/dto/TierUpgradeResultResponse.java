package com.sm_four_idiot.backend.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 승급 테스트 결과 응답 DTO
 */
@Getter
@Builder
public class TierUpgradeResultResponse {
    /** 통과 여부 */
    private boolean passed;

    /** 총 문제 수 */
    private int totalCount;

    /** 정답 수 */
    private int correctCount;

    /** 점수 (%) */
    private int score;

    /** 통과 시 승급된 티어, 실패 시 null */
    private String newTier;

    /** 남은 응시 횟수 */
    private int remainingAttempts;
}