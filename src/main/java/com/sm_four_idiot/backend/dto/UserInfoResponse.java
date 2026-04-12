package com.sm_four_idiot.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 유저 정보 응답 DTO
 */
@Getter
@AllArgsConstructor
public class UserInfoResponse {
    // private String email;
    private String nickname;
}