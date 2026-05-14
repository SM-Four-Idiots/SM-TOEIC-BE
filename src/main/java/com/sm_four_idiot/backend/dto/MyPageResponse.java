package com.sm_four_idiot.backend.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class MyPageResponse {
    private String nickname; // 닉네임
    private String email; // 이메일
    private String tier; // 현재 티어
    private int currentXp; // 현재 XP
    private int requiredXp; // 다음 티어까지 필요한 총 XP
    private int remainingXp; // 다음 티어까지 남은 XP
    private int maxStreak; // 최대 연속 학습
    private long totalWordsLearned; // 학습한 단어 수
    private int totalDailyQuestCompleted; // 일일 퀘스트 완료 횟수
    private int totalWeeklyQuestCompleted; // 주간 챌린지 완료 횟수
    private List<XpHistoryResponse> recentXpHistory; // 최근 Xp 획득 히스토리
    private boolean tierUpgradeEligible; // 승급 시험 도전 가능 여부
}