package com.sm_four_idiot.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.List;

/**
 * 테스트 결과 집계 요청 DTO
 */
@Getter
public class TestSummaryRequest {

    /** 테스트에 출제된 단어 ID 리스트 */
    @NotEmpty(message = "단어 ID 리스트를 입력해주세요")
    private List<Long> wordIds;
}