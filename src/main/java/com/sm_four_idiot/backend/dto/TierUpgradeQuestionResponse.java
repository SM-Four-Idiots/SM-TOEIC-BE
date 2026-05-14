package com.sm_four_idiot.backend.dto;

import com.sm_four_idiot.backend.domain.Word;
import lombok.Getter;

/**
 * 승급 테스트 문제 출제 응답 DTO
 * - type=0: 한글 뜻 제공, 영단어 맞히기 (힌트 포함)
 * - type=1: 영단어 제공, 한글 뜻 맞히기 (힌트 없음)
 */
@Getter
public class TierUpgradeQuestionResponse {
    private Long id;
    private int type;
    private String question;
    private String hint; // type=0일 때만 제공, type=1이면 null

    public TierUpgradeQuestionResponse(Word word, int type) {
        this.id = word.getId();
        this.type = type;
        this.question = type == 0 ? word.getMeaning() : word.getVoca();
        this.hint = type == 0 ? generateHint(word.getVoca()) : null;
    }

    /**
     * 영단어 힌트 생성
     * - 첫 글자와 마지막 글자만 공개, 나머지는 _ 로 표시
     * - 예: "accomplish" → "a________h"
     * @param voca 영단어
     * @return 힌트 문자열
     */
    private String generateHint(String voca) {
        if (voca.length() <= 2) return voca;
        return voca.charAt(0)
                + "_".repeat(voca.length() - 2)
                + voca.charAt(voca.length() - 1);
    }
}