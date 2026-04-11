package com.sm_four_idiot.backend.controller;

import com.sm_four_idiot.backend.dto.TestQuestionResponse;
import com.sm_four_idiot.backend.dto.TestRequest;
import com.sm_four_idiot.backend.dto.TestResponse;
import com.sm_four_idiot.backend.service.TestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 단어 테스트 관련 API 컨트롤러
 * - 문제 출제, 정답 제출
 */
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    /**
     * 테스트 문제 출제
     * GET /api/test/questions
     */
    @GetMapping("/questions")
    public ResponseEntity<List<TestQuestionResponse>> getTestQuestions() {
        return ResponseEntity.ok(testService.getTestQuestions());
    }

    /**
     * 정답 제출 및 채점
     * POST /api/test/submit
     */
    @PostMapping("/submit")
    public ResponseEntity<TestResponse> submitAnswer(
            @Valid @RequestBody TestRequest request) {
        return ResponseEntity.ok(testService.submitAnswer(request));
    }
}