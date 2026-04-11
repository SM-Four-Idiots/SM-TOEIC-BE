package com.sm_four_idiot.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

/**
 * 로그인 요청 DTO
 */
@Getter
public class LoginRequest {

    /** 이메일 */
    @NotBlank(message = "이메일을 입력해주세요")
    @Email(message = "이메일 형식이 올바르지 않습니다")
    private String email;

    /** 비밀번호 */
    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password;
}