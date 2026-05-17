package com.sm_four_idiot.backend.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 워들 게임 시작 응답 DTO
 */
@Getter
@Builder
public class WordleStartResponse {
    /** 정답 단어 ID */
    private Long wordId;
    /** 정답 단어 (프론트에서 채점 처리) */
    private String answer;
    /** 단어 길이 (항상 5) */
    private int length;
    /** 오늘 이미 플레이했는지 여부 */
    private boolean alreadyPlayed;
}