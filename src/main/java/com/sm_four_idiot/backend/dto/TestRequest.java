package com.sm_four_idiot.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

/**
 * 테스트 정답 제출 요청 DTO
 */
@Getter
public class TestRequest {

    /** 출제된 단어 ID (양수만 허용) */
    @NotNull(message = "단어 ID를 입력해주세요")
    @Positive(message = "단어 ID는 양수여야 합니다")
    private Long wordId;

    /** 문제 유형 (0: 영단어 맞히기, 1: 한글 뜻 맞히기) */
    @NotNull(message = "type을 입력해주세요")
    private Integer type;

    /** 사용자가 입력한 답 */
    @NotBlank(message = "답을 입력해주세요")
    private String answer;
}