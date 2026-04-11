package com.sm_four_idiot.backend.dto;

import com.sm_four_idiot.backend.domain.Word;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 단어 조회 응답 DTO
 */
@Getter
public class WordResponse {

    private Long id;
    private String english;
    private String meaning;
    private String category;
    private int tierLevel;
    private LocalDateTime createdAt;

    public WordResponse(Word word) {
        this.id = word.getId();
        this.english = word.getEnglish();
        this.meaning = word.getMeaning();
        this.category = word.getCategory();
        this.tierLevel = word.getTierLevel();
        this.createdAt = word.getCreatedAt();
    }
}