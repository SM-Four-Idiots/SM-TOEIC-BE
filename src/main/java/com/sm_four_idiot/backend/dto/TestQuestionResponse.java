package com.sm_four_idiot.backend.dto;

import com.sm_four_idiot.backend.domain.Word;
import lombok.Getter;

/**
 * 테스트 문제 출제 응답 DTO
 * - type=0: question에 한글 뜻, 영단어 맞히기
 * - type=1: question에 영단어, 한글 뜻 맞히기
 */
@Getter
public class TestQuestionResponse {

    private Long id;
    private int type;
    private String question;

    public TestQuestionResponse(Word word, int type) {
        this.id = word.getId();
        this.type = type;
        this.question = type == 0 ? word.getMeaning() : word.getEnglish();
    }
}