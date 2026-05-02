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
import com.sm_four_idiot.backend.dto.TestSummaryRequest;
import com.sm_four_idiot.backend.dto.TestSummaryResponse;

import java.util.Collections;
import java.util.List;
import java.util.Random;
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
     * - 문제마다 type(0/1) 랜덤 배정
     * - type=0: 한글 뜻 제공, 영단어 맞히기
     * - type=1: 영단어 제공, 한글 뜻 맞히기
     * @return 출제된 문제 리스트 (정답 제외)
     */
    @Transactional(readOnly = true)
    public List<TestQuestionResponse> getTestQuestions() {
        List<Word> allWords = wordRepository.findAll();
        Collections.shuffle(allWords);
        Random random = new Random();
        return allWords.stream()
                .limit(30)
                .map(word -> new TestQuestionResponse(word, random.nextInt(2)))
                .collect(Collectors.toList());
    }

    /**
     * 정답 제출 및 채점
     * - type=0: 영단어(english) 정답 비교, 대소문자 무시
     * - type=1: 한글 뜻(meaning) 정답 비교
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

        // type 유효성 검증
        int type = request.getType();
        if (type != 0 && type != 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "type은 0 또는 1이어야 합니다");
        }

        // type에 따라 정답 비교
        String correctAnswer;
        boolean isCorrect;
        if (type == 0) {
            // 영단어 맞히기: 대소문자 무시
            correctAnswer = word.getEnglish();
            isCorrect = correctAnswer.equalsIgnoreCase(request.getAnswer().trim());
        } else {
            // 한글 뜻 맞히기: 앞뒤 공백만 제거
            correctAnswer = word.getMeaning();
            isCorrect = correctAnswer.equals(request.getAnswer().trim());
        }

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
                correctAnswer,
                isCorrect ? "정답입니다!" : "오답입니다. 정답: " + correctAnswer
        );
    }


    /**
     * 테스트 결과 집계
     * - 제출된 wordId 기준으로 DB의 TestResult 조회
     * - 프론트 조작 불가 (서버 DB 기반 집계)
     * @param request wordId 리스트
     * @param email   현재 로그인한 사용자 이메일
     * @return 총 문제 수, 맞은 개수, 틀린 개수
     */
    @Transactional(readOnly = true)
    public TestSummaryResponse getTestSummary(TestSummaryRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "로그인이 필요합니다"));

        List<TestResult> results = testResultRepository
                .findLatestByUserAndWordIdIn(user, request.getWordIds());

        int total = results.size();
        int correct = (int) results.stream().filter(TestResult::isCorrect).count();
        int wrong = total - correct;

        return new TestSummaryResponse(total, correct, wrong);
    }
}