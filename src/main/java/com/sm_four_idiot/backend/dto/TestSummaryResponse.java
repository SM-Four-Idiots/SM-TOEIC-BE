package com.sm_four_idiot.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 테스트 결과 집계 응답 DTO
 */
@Getter
@AllArgsConstructor
public class TestSummaryResponse {

    /** 총 문제 수 */
    private int total;

    /** 맞은 개수 */
    private int correct;

    /** 틀린 개수 */
    private int wrong;
}