package com.sm_four_idiot.backend.controller;

import com.sm_four_idiot.backend.dto.UserInfoResponse;
import com.sm_four_idiot.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 유저 정보 관련 API
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * GET /api/user/info
     * - 액세스 토큰으로 유저 정보 조회
     */
    @GetMapping("/info")
    public ResponseEntity<UserInfoResponse> getUserInfo(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getUserInfo(userDetails.getUsername()));
    }
}