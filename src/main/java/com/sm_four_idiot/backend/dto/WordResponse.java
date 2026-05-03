package com.sm_four_idiot.backend.dto;

import com.sm_four_idiot.backend.domain.Word;
import com.sm_four_idiot.backend.domain.Word.Tier;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 단어 조회 응답 DTO
 */
@Getter
public class WordResponse {

    private Long id;
    private String voca;
    private String meaning;
    private String category;
    private Tier tier;
    private LocalDateTime createdAt;

    public WordResponse(Word word) {
        this.id = word.getId();
        this.voca = word.getVoca();
        this.meaning = word.getMeaning();
        this.category = word.getCategory();
        this.tier = word.getTier();
        this.createdAt = word.getCreatedAt();
    }
}