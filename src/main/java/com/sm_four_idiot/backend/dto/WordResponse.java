package com.sm_four_idiot.backend.dto;

import com.sm_four_idiot.backend.domain.Word;
import com.sm_four_idiot.backend.domain.Tier;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    private String exampleSentence;
    private LocalDateTime createdAt;

    @JsonProperty("isBookmarked")
    private boolean isBookmarked;

    /** 북마크 여부 모를 때 (기본값 false) */
    public WordResponse(Word word) {
        this.id = word.getId();
        this.voca = word.getVoca();
        this.meaning = word.getMeaning();
        this.category = word.getCategory();
        this.tier = word.getTier();
        this.exampleSentence = word.getExampleSentence();
        this.createdAt = word.getCreatedAt();
        this.isBookmarked = false;
    }

    /** 북마크 여부 알 때 */
    public WordResponse(Word word, boolean isBookmarked) {
        this(word);
        this.isBookmarked = isBookmarked;
    }
}