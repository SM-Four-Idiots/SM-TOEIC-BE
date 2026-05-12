package com.sm_four_idiot.backend.controller;

import com.sm_four_idiot.backend.domain.RewardType;
import com.sm_four_idiot.backend.domain.User;
import com.sm_four_idiot.backend.dto.RewardClaimRequest;
import com.sm_four_idiot.backend.dto.RewardClaimResponse;
import com.sm_four_idiot.backend.dto.RewardLimitsResponse;
import com.sm_four_idiot.backend.service.RewardService;
import com.sm_four_idiot.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
     * 역할: 로그인한 사용자의 모든 보상 타입(퀘스트, 단어 테스트 등)에 대한 현재 상한 상태를 조회합니다.
     * 파라미터: 없음 (Authorization 헤더의 JWT 토큰으로 유저 식별)
     * 반환: 보상 타입, 현재까지 받은 포인트, 일일 최대 상한선, 상한 초과 여부를 담은 리스트
     *
     * [Back-end]
     * DB 흐름: UserDetails에서 이메일을 추출해 DB에서 User를 조회합니다.
     * 이후 RewardType Enum의 모든 값을 순회하면서, 각 타입별로 DB(reward_limit 테이블)를 조회(SELECT)하여 DTO로 변환합니다.
     */
    @GetMapping("/limits")
    public ResponseEntity<List<RewardLimitsResponse>> getRewardLimits(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());

        List<RewardLimitsResponse> allRewardLimits = Arrays.stream(RewardType.values())
                .map(rewardType -> rewardService.getRewardLimits(user, rewardType))
                .collect(Collectors.toList());

        return ResponseEntity.ok(allRewardLimits);
    }

    /**
     * [Front-end]
     * 역할: 특정 행동 완료 후 보상(포인트) 획득을 서버에 요청합니다.
     * 파라미터: RewardClaimRequest (rewardType: 보상 종류, amount: 획득할 포인트 양)
     * 반환: 보상 획득 성공 여부, 현재 총 보유 포인트, 이번에 획득한 포인트 양
     *
     * [Back-end]
     * DB 흐름: 클라이언트로부터 들어온 JSON 바디를 @Valid로 1차 검증(음수 불가 등)합니다.
     * 유효한 요청일 경우 트랜잭션을 시작하여 User 데이터와 RewardLimit 데이터를 DB에서 잠금(Lock) 및 갱신(UPDATE)합니다.
     */
    @PostMapping("/claim")
    public ResponseEntity<RewardClaimResponse> claimReward(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody RewardClaimRequest request) { // @Valid 추가
            
        User user = userService.findByEmail(userDetails.getUsername());
        RewardClaimResponse response = rewardService.claimReward(user, request);
        
        return ResponseEntity.ok(response);
    }
}