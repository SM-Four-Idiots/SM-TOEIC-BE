package com.sm_four_idiot.backend.controller;

import com.sm_four_idiot.backend.domain.RewardType;
import com.sm_four_idiot.backend.domain.User;
import com.sm_four_idiot.backend.dto.ResponseDto;
import com.sm_four_idiot.backend.dto.RewardClaimRequest;
import com.sm_four_idiot.backend.dto.RewardClaimResponse;
import com.sm_four_idiot.backend.dto.RewardLimitsResponse;
import com.sm_four_idiot.backend.service.RewardService;
import com.sm_four_idiot.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * [Front-end] 사용자의 보상 대시보드 상태를 조회하거나, 보상 획득 버튼을 눌렀을 때 호출되는 API 컨트롤러입니다.
 * [Back-end] 웹 계층(Presentation Layer)을 담당하며 HTTP 요청을 받아 Service 계층으로 비즈니스 로직 처리를 위임합니다.
 */
@RestController
@RequestMapping("/api/rewards")
@RequiredArgsConstructor
public class RewardController {

    private final RewardService rewardService;
    private final UserService userService;

    /**
     * [Front-end]
     * 역할: 로그인한 사용자의 모든 보상 타입에 대한 현재 상한 상태를 조회합니다.
     * 파라미터: 없음 (Authorization 헤더의 JWT 토큰으로 유저 식별)
     * 반환: 보상 타입, 현재까지 받은 포인트, 일일 최대 상한선, 상한 초과 여부를 담은 리스트
     */
    @GetMapping("/limits")
    public ResponseEntity<ResponseDto<List<RewardLimitsResponse>>> getRewardLimits(
            @AuthenticationPrincipal String email) {
        User user = userService.findByEmail(email);

        List<RewardLimitsResponse> allRewardLimits = Arrays.stream(RewardType.values())
                .map(rewardType -> rewardService.getRewardLimits(user, rewardType))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ResponseDto.success(allRewardLimits));
    }

    /**
     * [Front-end]
     * 역할: 특정 행동 완료 후 보상(포인트) 획득을 서버에 요청합니다.
     * 파라미터: RewardClaimRequest (rewardType: 보상 종류, amount: 획득할 포인트 양)
     * 반환: 보상 획득 성공 여부, 현재 총 보유 포인트, 이번에 획득한 포인트 양
     */
    @PostMapping("/claim")
    public ResponseEntity<ResponseDto<RewardClaimResponse>> claimReward(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody RewardClaimRequest request) {

        User user = userService.findByEmail(email);
        RewardClaimResponse response = rewardService.claimReward(user, request);

        return ResponseEntity.ok(ResponseDto.success(response));
    }
}