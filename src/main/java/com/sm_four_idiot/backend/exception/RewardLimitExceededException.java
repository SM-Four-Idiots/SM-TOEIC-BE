package com.sm_four_idiot.backend.exception;

/**
 * [Back-end] 보상 한도를 초과했을 때 발생하는 커스텀 예외입니다.
 * 비즈니스 로직 처리 중 한도가 넘으면 이 예외를 던지고(throw), GlobalExceptionHandler가 가로채어 응답을 만듭니다.
 */
public class RewardLimitExceededException extends RuntimeException {
    public RewardLimitExceededException(String message) {
        super(message);
    }
}