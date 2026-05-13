package com.sm_four_idiot.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * [DTO] 공통 API 응답 래퍼 (Wrapper)
 * * [Frontend] HTTP Status 외에 커스텀 상태 코드와 메시지, 실제 페이로드(data)를 일관성 있게 전달받기 위한 객체입니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto<T> {
    private int status;
    private String message;
    private T data;

    public static <T> ResponseDto<T> success(T data) {
        return new ResponseDto<>(HttpStatus.OK.value(), "성공", data);
    }

    public static <T> ResponseDto<T> error(HttpStatus httpStatus, String message) {
        return new ResponseDto<>(httpStatus.value(), message, null);
    }

    public static <T> ResponseDto<T> error(String message) {
        return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), message, null);
    }
}