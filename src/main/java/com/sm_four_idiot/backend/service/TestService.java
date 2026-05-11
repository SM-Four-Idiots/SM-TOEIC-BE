package com.sm_four_idiot.backend.service;

import com.sm_four_idiot.backend.domain.TestResult;
import com.sm_four_idiot.backend.domain.User;
import com.sm_four_idiot.backend.domain.Word;
import com.sm_four_idiot.backend.domain.WrongWord;
import com.sm_four_idiot.backend.dto.TestQuestionResponse;
import com.sm_four_idiot.backend.dto.TestRequest;
import com.sm_four_idiot.backend.dto.TestResponse;
import com.sm_four_idiot.backend.dto.TestSummaryRequest;
import com.sm_four_idiot.backend.dto.TestSummaryResponse;
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
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestService {

    private final WordRepository wordRepository;
    private final UserRepository userRepository;
    private final TestResultRepository testResultRepository;
    private final WrongWordRepository wrongWordRepository;

    /**
     * 테스트 문제 출제
     * - 전체 단어 중 랜덤으로 30개 출제
     * - 문제마다 type(0/1) 랜덤 배정 후 DB에 저장
     * - type=0: 한글 뜻 제공, 영단어 맞히기
     * - type=1: 영단어 제공, 한글 뜻 맞히기
     */
    @Transactional
    public List<TestQuestionResponse> getTestQuestions() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "로그인이 필요합니다"));

        List<Word> allWords = wordRepository.findAll();
        Collections.shuffle(allWords);
        Random random = new Random();

        return allWords.stream()
                .limit(30)
                .map(word -> {
                    int type = random.nextInt(2);

                    // type을 DB에 저장 (채점 시 서버에서 꺼내 사용)
                    testResultRepository.save(TestResult.builder()
                            .user(user)
                            .word(word)
                            .type(type)
                            .isCorrect(false) // 아직 채점 전
                            .build());

                    return new TestQuestionResponse(word, type);
                })
                .collect(Collectors.toList());
    }

    /**
     * 정답 제출 및 채점
     * - DB에 저장된 type 기준으로 채점 (클라이언트 조작 불가)
     * - type=0: 영단어(Voca) 정답 비교, 대소문자 무시
     * - type=1: 한글 뜻(meaning) 정답 비교
     */
    @Transactional
    public TestResponse submitAnswer(TestRequest request) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "로그인이 필요합니다"));

        Word word = wordRepository.findById(request.getWordId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "단어를 찾을 수 없습니다"));

        // DB에서 해당 유저 + 단어의 가장 최근 TestResult 조회 (type 꺼내기)
        TestResult pendingResult = testResultRepository
                .findTopByUserAndWordOrderByTestedAtDesc(user, word)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "출제되지 않은 문제입니다"));

        int type = pendingResult.getType();

        // type에 따라 정답 비교
        String correctAnswer;
        boolean isCorrect;
        if (type == 0) {
            correctAnswer = word.getVoca();
            isCorrect = correctAnswer.equalsIgnoreCase(request.getAnswer().trim());
        } else {
            correctAnswer = word.getMeaning();
            isCorrect = correctAnswer.equals(request.getAnswer().trim());
        }

        // 기존 TestResult 업데이트 (isCorrect 갱신)
        pendingResult.updateResult(isCorrect);

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