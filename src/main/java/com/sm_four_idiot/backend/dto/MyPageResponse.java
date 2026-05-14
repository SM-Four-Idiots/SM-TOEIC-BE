package com.sm_four_idiot.backend.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class MyPageResponse {
    private String nickname;
    private String email;
    private String tier;
    private int currentXp;
    private int requiredXp;
    private int remainingXp;
    private int maxStreak;
    private long totalWordsLearned;
    private int totalDailyQuestCompleted;
    private int totalWeeklyQuestCompleted;
    private List<XpHistoryResponse> recentXpHistory;
}