package com.sm_four_idiot.backend.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

/**
 * 오답 노트 목록 응답 DTO
 */
@Getter
@Builder
public class WrongWordListResponse {
    /** 오답 노트 단어 개수 */
    private int totalCount;
    /** 오답 단어 목록 */
    private List<WrongWordResponse> words;
}