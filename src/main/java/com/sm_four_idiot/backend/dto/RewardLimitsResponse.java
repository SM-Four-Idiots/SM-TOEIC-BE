package com.sm_four_idiot.backend.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RewardLimitsResponse {
    private String rewardType; //보상 종류
    private int currentClaimed; //현재까지 획득한 보상
    private int dailyLimit;     //일일 최대 획득 가능 보상
    private boolean isLimitExceeded; //한도 초과 여부
}