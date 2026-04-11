package com.sm_four_idiot.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 테스트 결과 응답 DTO
 */
@Getter
@AllArgsConstructor
public class TestResponse {

    /** 정답 여부 */
    private boolean correct;

    /** 정답 단어 */
    private String correctAnswer;

    /** 결과 메시지 */
    private String message;
}