package com.sm_four_idiot.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.util.List;

/**
 * 승급 테스트 답안 제출 요청 DTO
 * - 30개 답안을 한 번에 제출
 */
@Getter
public class TierUpgradeResultRequest {

    @NotEmpty(message = "답안 리스트를 입력해주세요")
    private List<AnswerItem> answers;

    /**
     * 개별 답안 항목
     */
    @Getter
    public static class AnswerItem {
        @NotNull(message = "단어 ID를 입력해주세요")
        @Positive(message = "단어 ID는 양수여야 합니다")
        private Long wordId;

        @NotNull(message = "답을 입력해주세요")
        private String answer;
    }
}