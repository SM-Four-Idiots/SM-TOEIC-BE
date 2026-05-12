package com.sm_four_idiot.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * [Front-end] API 호출 시 서버 에러가 발생했을 때, 항상 일관된 형태의 JSON 예외 응답을 받게 해주는 핸들러입니다.
 * 모든 에러 응답은 { "success": false, "message": "에러 내용" } 형태로 반환됩니다.
 *
 * [Back-end] 컨트롤러나 서비스에서 try-catch를 덕지덕지 붙이지 않아도 여기서 전역적으로 에러를 캐치해 HTTP 상태 코드와 매핑합니다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * [Front-end] 일일 한도를 초과하여 보상을 요청했을 때 403 Forbidden 상태 코드와 함께 안내 메시지를 반환합니다.
     */
    @ExceptionHandler(RewardLimitExceededException.class)
    public ResponseEntity<Map<String, Object>> handleRewardLimitExceeded(RewardLimitExceededException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * [Front-end] 요청 파라미터가 잘못되었을 때(예: 보상 포인트가 음수) 400 Bad Request 에러를 반환합니다.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}