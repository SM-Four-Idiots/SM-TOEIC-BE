package com.sm_four_idiot.backend.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class XpHistoryResponse {
    private int xpGained;
    private String reason;
    private LocalDateTime earnedAt;
}