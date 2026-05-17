package com.sm_four_idiot.backend.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

/**
 * 랭킹 보드 응답 DTO
 */
@Getter
@Builder
public class RankingResponse {

    /** 내 순위 정보 */
    private RankingEntry myRank;

    /** 전체 랭킹 목록 */
    private List<RankingEntry> rankings;

    /**
     * 개별 랭킹 항목
     */
    @Getter
    @Builder
    public static class RankingEntry {
        /** 순위 */
        private int rank;
        /** 닉네임 */
        private String nickname;
        /** 티어 */
        private String tier;
        /** 누적 XP */
        private int xp;
        /** 본인 여부 */
        private boolean isMe;
    }
}