package com.sm_four_idiot.backend.dto;

import com.sm_four_idiot.backend.domain.Word.Tier;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * 단어 추가/수정 요청 DTO
 */
@Getter
public class WordRequest {

    /** 영어 단어 */
    @NotBlank(message = "영어 단어를 입력해주세요")
    private String voca;

    /** 한글 뜻 */
    @NotBlank(message = "뜻을 입력해주세요")
    private String meaning;

    /** 카테고리 */
    @NotBlank(message = "카테고리를 입력해주세요")
    private String category;

    /** 난이도 티어 (BRONZE / SILVER / GOLD / PLATINUM / DIAMOND) */
    @NotNull(message = "티어를 입력해주세요")
    private Tier tier;
}