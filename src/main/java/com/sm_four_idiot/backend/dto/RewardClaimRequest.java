package com.sm_four_idiot.backend.dto;

import com.sm_four_idiot.backend.domain.RewardType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [Front-end] 보상 획득 API(/api/rewards/claim) 호출 시 보내야 하는 Request Body JSON 구조입니다.
 */
@Getter
@NoArgsConstructor
public class RewardClaimRequest {
    
    // 프론트엔드는 반드시 RewardType Enum에 있는 정확한 문자열을 보내야 합니다.
    @NotNull(message = "보상 타입은 필수입니다.")
    private RewardType rewardType;

    // 프론트엔드에서 실수로 0이나 음수 포인트를 전송하는 것을 백엔드 진입점에서 차단합니다.
    @Min(value = 1, message = "획득 요청량은 1 이상이어야 합니다.")
    private int amount;
}