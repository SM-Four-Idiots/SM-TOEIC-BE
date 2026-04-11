package com.sm_four_idiot.backend.service;

import com.sm_four_idiot.backend.domain.TestResult;
import com.sm_four_idiot.backend.domain.User;
import com.sm_four_idiot.backend.domain.Word;
import com.sm_four_idiot.backend.domain.WrongWord;
import com.sm_four_idiot.backend.dto.TestQuestionResponse;
import com.sm_four_idiot.backend.dto.TestRequest;
import com.sm_four_idiot.backend.dto.TestResponse;
import com.sm_four_idiot.backend.repository.TestResultRepository;
import com.sm_four_idiot.backend.repository.UserRepository;
import com.sm_four_idiot.backend.repository.WordRepository;
import com.sm_four_idiot.backend.repository.WrongWordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 단어 테스트 관련 비즈니스 로직
 * - 문제 출제, 정답 채점, 오답 저장
 */
@Service
@RequiredArgsConstructor
public class TestService {

    private final WordRepository wordRepository;
    private final UserRepository userRepository;
    private final TestResultRepository testResultRepository;
    private final WrongWordRepository wrongWordRepository;

    /**
     * 테스트 문제 출제
     * - 전체 단어 중 랜덤으로 10개 출제
     * - english(정답) 필드 제외하여 정답 노출 방지
     * @return 출제된 단어 리스트 (정답 제외)
     */
    @Transactional(readOnly = true)
    public List<TestQuestionResponse> getTestQuestions() {
        List<Word> allWords = wordRepository.findAll();
        Collections.shuffle(allWords);
        return allWords.stream()
                .limit(10)
                .map(TestQuestionResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 정답 제출 및 채점
     * - 대소문자 무시하여 정답 비교
     * - 오답 시 WrongWord에 저장
     * @param request 정답 제출 요청 DTO
     * @return 채점 결과
     */
    @Transactional
    public TestResponse submitAnswer(TestRequest request) {
        // 인증 정보 null 체크
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다");
        }

        // 현재 로그인한 사용자 조회
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "로그인이 필요합니다"));

        // 단어 조회
        Word word = wordRepository.findById(request.getWordId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "단어를 찾을 수 없습니다"));

        // 대소문자 무시하여 정답 비교
        boolean isCorrect = word.getEnglish()
                .equalsIgnoreCase(request.getAnswer().trim());

        // 테스트 결과 저장
        testResultRepository.save(TestResult.builder()
                .user(user)
                .word(word)
                .isCorrect(isCorrect)
                .build());

        // 오답 시 WrongWord 저장
        if (!isCorrect) {
            wrongWordRepository.save(WrongWord.builder()
                    .user(user)
                    .word(word)
                    .build());
        }

        return new TestResponse(
                isCorrect,
                word.getEnglish(),
                isCorrect ? "정답입니다!" : "오답입니다. 정답: " + word.getEnglish()
        );
    }
}