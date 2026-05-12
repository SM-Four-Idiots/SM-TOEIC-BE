package com.sm_four_idiot.backend.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RewardClaimResponse {
    private boolean success;
    private int newTotalPoints;
    private int claimedAmount;
    private String message;
}