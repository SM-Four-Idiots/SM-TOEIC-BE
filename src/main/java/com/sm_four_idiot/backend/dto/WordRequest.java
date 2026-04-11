package com.sm_four_idiot.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

/**
 * 단어 추가/수정 요청 DTO
 */
@Getter
public class WordRequest {

    /** 영어 단어 */
    @NotBlank(message = "영어 단어를 입력해주세요")
    private String english;

    /** 한글 뜻 */
    @NotBlank(message = "뜻을 입력해주세요")
    private String meaning;

    /** 카테고리 */
    @NotBlank(message = "카테고리를 입력해주세요")
    private String category;

    /** 난이도 티어 (1~5) */
    @Min(value = 1, message = "티어는 최소 1입니다")
    @Max(value = 5, message = "티어는 최대 5입니다")
    private int tierLevel;
}