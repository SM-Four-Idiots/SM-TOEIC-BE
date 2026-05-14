package com.sm_four_idiot.backend.service;

import com.sm_four_idiot.backend.config.TierConfig;
import com.sm_four_idiot.backend.domain.*;
import com.sm_four_idiot.backend.dto.TierUpgradeQuestionResponse;
import com.sm_four_idiot.backend.dto.TierUpgradeResultRequest;
import com.sm_four_idiot.backend.dto.TierUpgradeResultResponse;
import com.sm_four_idiot.backend.repository.TestResultRepository;
import com.sm_four_idiot.backend.repository.UserRepository;
import com.sm_four_idiot.backend.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 티어 승급 테스트 비즈니스 로직
 */
@Service
@RequiredArgsConstructor
public class TierUpgradeService {

    private final UserRepository userRepository;
    private final WordRepository wordRepository;
    private final TestResultRepository testResultRepository;

    private static final int QUESTION_COUNT = 30;
    private static final double PASS_THRESHOLD = 0.7;

    /**
     * 승급 테스트 문제 출제
     * - 승급 자격 및 응시 가능 여부 체크
     * - 현재 티어 단어 30개 랜덤 출제
     * - type(0/1) 랜덤 배정 후 DB 저장 (클라이언트 조작 방지)
     * @param email JWT에서 추출한 이메일
     * @return 문제 리스트
     */
    @Transactional
    public List<TierUpgradeQuestionResponse> getQuestions(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다"));

        // 승급 자격 및 응시 가능 여부 체크
        if (!user.canAttemptTierUpgrade()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "승급 테스트 응시 자격이 없거나 오늘 응시 횟수를 초과했습니다");
        }

        // 현재 티어 단어 조회 후 랜덤 30개 추출
        List<Word> words = wordRepository.findByTier(user.getTier());
        if (words.size() < QUESTION_COUNT) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "출제할 단어가 부족합니다");
        }
        Collections.shuffle(words);
        Random random = new Random();

        return words.stream()
                .limit(QUESTION_COUNT)
                .map(word -> {
                    int type = random.nextInt(2);

                    // type을 DB에 저장 (채점 시 서버에서 꺼내 사용)
                    testResultRepository.save(TestResult.builder()
                            .user(user)
                            .word(word)
                            .type(type)
                            .isCorrect(false)
                            .build());

                    return new TierUpgradeQuestionResponse(word, type);
                })
                .collect(Collectors.toList());
    }

    /**
     * 승급 테스트 결과 제출 및 채점
     * - DB에서 type 조회 후 채점 (클라이언트 조작 불가)
     * - 70% 이상 정답 시 티어 승급
     * - 실패 시 응시 횟수 증가
     * @param email JWT에서 추출한 이메일
     * @param request 30개 답안 리스트
     * @return 채점 결과
     */
    @Transactional
    public TierUpgradeResultResponse submitResult(String email, TierUpgradeResultRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다"));

        int correctCount = 0;

        for (TierUpgradeResultRequest.AnswerItem item : request.getAnswers()) {

            // DB에서 type 조회
            TestResult pendingResult = testResultRepository
                    .findTopByUserAndWordOrderByTestedAtDesc(user,
                            wordRepository.findById(item.getWordId())
                                    .orElseThrow(() -> new ResponseStatusException(
                                            HttpStatus.NOT_FOUND, "단어를 찾을 수 없습니다")))
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "출제되지 않은 문제입니다"));

            Word actualWord = pendingResult.getWord();
            int type = pendingResult.getType();

            // type에 따라 채점
            String correctAnswer;
            boolean isCorrect;
            if (type == 0) {
                correctAnswer = actualWord.getVoca();
                isCorrect = correctAnswer.equalsIgnoreCase(item.getAnswer().trim());
            } else {
                correctAnswer = actualWord.getMeaning();
                isCorrect = correctAnswer.equals(item.getAnswer().trim());
            }

            pendingResult.updateResult(isCorrect);
            if (isCorrect) correctCount++;
        }

        int totalCount = request.getAnswers().size();
        int score = (int) ((double) correctCount / totalCount * 100);
        boolean passed = score >= (int)(PASS_THRESHOLD * 100);

        String newTier = null;
        if (passed) {
            // 통과 시 티어 승급
            user.upgradeTier();
            newTier = user.getTier().name();
            userRepository.save(user);
        } else {
            // 실패 시 응시 횟수 증가
            user.incrementTierUpgradeAttempt();
            userRepository.save(user);
        }

        // 남은 응시 횟수 계산
        int remainingAttempts = TierConfig.TIER_UPGRADE_ATTEMPT_LIMIT
                - user.getTierUpgradeAttemptCount();

        return TierUpgradeResultResponse.builder()
                .passed(passed)
                .totalCount(totalCount)
                .correctCount(correctCount)
                .score(score)
                .newTier(newTier)
                .remainingAttempts(Math.max(0, remainingAttempts))
                .build();
    }
}