package com.sm_four_idiot.backend.dto;

import com.sm_four_idiot.backend.domain.Word;
import lombok.Getter;

/**
 * 테스트 문제 출제 응답 DTO
 * - english(정답) 필드 제외하여 정답 노출 방지
 */
@Getter
public class TestQuestionResponse {

    private Long id;
    private String meaning;
    private String category;
    private int tierLevel;

    public TestQuestionResponse(Word word) {
        this.id = word.getId();
        this.meaning = word.getMeaning();
        this.category = word.getCategory();
        this.tierLevel = word.getTierLevel();
    }
}