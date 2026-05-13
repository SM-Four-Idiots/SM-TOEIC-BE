package com.sm_four_idiot.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RewardLimitsResponse {
    private String rewardType; //보상 종류
    private int currentClaimed; //현재까지 획득한 보상
    private int dailyLimit;     //일일 최대 획득 가능 보상

    // [Fix - 이미지 7] JSON 직렬화 시 키값이 "limitExceeded"로 변형되는 것을 막고 "isLimitExceeded"로 강제 고정
    @JsonProperty("isLimitExceeded")
    private boolean isLimitExceeded; //한도 초과 여부
}