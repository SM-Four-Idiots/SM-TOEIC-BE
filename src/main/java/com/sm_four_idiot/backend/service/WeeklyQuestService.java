package com.sm_four_idiot.backend.service;

import com.sm_four_idiot.backend.domain.User;
import com.sm_four_idiot.backend.domain.WeeklyQuest;
import com.sm_four_idiot.backend.domain.WeeklyQuestStatus;
import com.sm_four_idiot.backend.dto.WeeklyQuestResponse;
import com.sm_four_idiot.backend.repository.UserRepository;
import com.sm_four_idiot.backend.repository.WeeklyQuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.temporal.TemporalAdjusters;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * 주간 퀘스트 비즈니스 로직
 */
@Service
@RequiredArgsConstructor
public class WeeklyQuestService {

    private final WeeklyQuestRepository weeklyQuestRepository;
    private final UserRepository userRepository;

    private static final int TARGET_COUNT = 7;
    private static final int REWARD_XP = 120;



    private LocalDate getWeekStartDate() {
        LocalDate today = LocalDate.now();
        return today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    /**
     * 주간 퀘스트 조회
     * - 이번 주 데이터 없으면 Lazy 생성
     * @param email JWT에서 추출한 이메일
     * @return 주간 퀘스트 응답
     */
    @Transactional
    public WeeklyQuestResponse getWeeklyQuest(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다"));

        LocalDate weekStartDate = getWeekStartDate();

        try {
            weeklyQuestRepository.insertIfNotExists(user.getId(), weekStartDate);
        } catch (DataIntegrityViolationException e) {
            // 동시 요청으로 중복 생성 시도 방어
        }

        WeeklyQuest weeklyQuest = weeklyQuestRepository
                .findByUserAndWeekStartDate(user, weekStartDate)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "주간 퀘스트 조회 실패"));

        return WeeklyQuestResponse.builder()
                .completedCount(weeklyQuest.getCompletedCount())
                .targetCount(TARGET_COUNT)
                .status(weeklyQuest.getStatus())
                .rewardXp(REWARD_XP)
                .build();
    }

    /**
     * 일일 퀘스트 완료 시 주간 퀘스트 진행도 증가
     * - 7회 달성 시 XP 보상 지급
     * @param userId 유저 ID
     */
    @Transactional
    public void incrementWeeklyProgress(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다"));

        LocalDate weekStartDate = getWeekStartDate();

        weeklyQuestRepository.insertIfNotExists(user.getId(), weekStartDate);

        WeeklyQuest weeklyQuest = weeklyQuestRepository
                .findByUserAndWeekStartDate(user, weekStartDate)
                .orElseThrow();

        if (weeklyQuest.getStatus() != WeeklyQuestStatus.IN_PROGRESS) return;

        weeklyQuest.incrementCount();

        if (weeklyQuest.getStatus() == WeeklyQuestStatus.COMPLETED) {
            userRepository.incrementUserXp(userId, REWARD_XP);
            weeklyQuest.reward();
        }

        weeklyQuestRepository.save(weeklyQuest);
    }
}