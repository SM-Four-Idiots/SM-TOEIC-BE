package com.sm_four_idiot.backend.config;

import com.sm_four_idiot.backend.domain.Tier;

/**
 * 티어 승급 관련 설정값
 * - XP 임계값 변경 시 이 파일만 수정하면 됨
 */
public class TierConfig {

    /** 일일 퀘스트 최대 횟수 */
    public static final int DAILY_QUEST_LIMIT = 3;

    /** 승급 테스트 하루 최대 응시 횟수 */
    public static final int TIER_UPGRADE_ATTEMPT_LIMIT = 3;

    /** 티어별 승급에 필요한 XP */
    public static int getRequiredXp(Tier tier) {
        return switch (tier) {
            case BRONZE -> 100;
            case SILVER -> 200;
            case GOLD -> 300;
            case PLATINUM -> 400;
            case DIAMOND -> Integer.MAX_VALUE; // 최고 티어
        };
    }

    /** 다음 티어 반환 */
    public static Tier nextTier(Tier tier) {
        return switch (tier) {
            case BRONZE -> Tier.SILVER;
            case SILVER -> Tier.GOLD;
            case GOLD -> Tier.PLATINUM;
            case PLATINUM -> Tier.DIAMOND;
            case DIAMOND -> Tier.DIAMOND; // 이미 최고 티어
        };
    }
}