package com.sm_four_idiot.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/**
 * 회원가입 요청 DTO
 */
@Getter
public class SignUpRequest {

    /** 이메일 (형식 검증) */
    @NotBlank(message = "이메일을 입력해주세요")
    @Email(message = "이메일 형식이 올바르지 않습니다")
    private String email;

    /** 비밀번호 (최소 8자) */
    @NotBlank(message = "비밀번호를 입력해주세요")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
    private String password;

    /** 닉네임 */
    @NotBlank(message = "닉네임을 입력해주세요")
    private String nickname;

}