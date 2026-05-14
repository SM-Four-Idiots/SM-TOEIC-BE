package com.sm_four_idiot.backend.service;

import com.sm_four_idiot.backend.domain.*;
import com.sm_four_idiot.backend.dto.QuestDashboardResponse;
import com.sm_four_idiot.backend.dto.QuestResponse;
import com.sm_four_idiot.backend.event.QuestProgressEvent;
import com.sm_four_idiot.backend.repository.UserQuestRepository;
import com.sm_four_idiot.backend.repository.UserRepository;
import com.sm_four_idiot.backend.repository.XpHistoryRepository;
import com.sm_four_idiot.backend.config.TierConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * [Service] 퀘스트 관련 비즈니스 로직 처리 클래스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuestService {

    private final UserQuestRepository userQuestRepository;
    private final UserRepository userRepository;
    private final XpHistoryRepository xpHistoryRepository;

    // 초기 생성되는 일일 퀘스트의 메타데이터 (제목, 목표량, 보상 경험치) 설정 맵
    private static final Map<QuestType, QuestConfig> DEFAULT_QUESTS = Map.of(
            QuestType.WORD_LEARN, new QuestConfig("오늘의 단어 50개 학습하기", 50, 100),
            QuestType.WORDLE_PLAY, new QuestConfig("워들 1회 참여", 1, 50),
            QuestType.TEST_COMPLETE, new QuestConfig("테스트 1회 완료", 1, 70)
    );

    @Getter
    @AllArgsConstructor
    private static class QuestConfig {
        private final String title;
        private final int targetProgress;
        private final int xpReward;
    }

    /**
     * [Backend] 대시보드 조회 및 퀘스트 자정 초기화 생성 로직
     * 흐름: 
     * 1. 오늘 시작 시간(자정) 기준 이전 데이터 삭제 쿼리 즉시 실행.
     * 2. 삭제 후 남아있는 오늘자 퀘스트 목록 조회.
     * 3. 목록이 없으면 새로 퀘스트 메타데이터를 기반으로 생성 후 반환.
     */
    @Transactional
    public QuestDashboardResponse getQuestDashboard(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        userQuestRepository.deleteOldQuests(user, startOfToday);

        List<UserQuest> userQuests = userQuestRepository.findByUser(user);

        // 오늘 생성된 퀘스트가 없는 경우 새로 생성 시도
        if (userQuests.isEmpty()) {
            try {
                // [Fix 1] Race Condition 방어 로직: 동시에 Insert 시도 시 Unique 제약조건에 의해 에러 발생
                // 자정에 프론트엔드에서 다중 새로고침 요청이 들어올 때 데이터 중복 생성을 방지합니다.
                userQuests = createDefaultDailyQuests(user);
            } catch (DataIntegrityViolationException e) {
                // 다른 스레드가 간발의 차로 먼저 퀘스트를 생성했다면 쿨하게 다시 Select
                log.info("동시성으로 인한 퀘스트 중복 생성 시도 방어됨. userId: {}", userId);
                userQuests = userQuestRepository.findByUser(user);
            }
        }

        // 도메인 모델을 프론트엔드 전송용 DTO로 매핑
        List<QuestResponse> questResponses = userQuests.stream()
                .map(this::mapToQuestResponse)
                .collect(Collectors.toList());

        return QuestDashboardResponse.builder()
                .dailyXp(user.getXp())
                .quests(questResponses)
                .build();
    }

    /**
     * [Backend] 초기 일일 퀘스트 엔티티 리스트를 생성하고 저장하는 헬퍼 메서드
     */
    private List<UserQuest> createDefaultDailyQuests(User user) {
        List<UserQuest> newQuests = DEFAULT_QUESTS.entrySet().stream()
                .map(entry -> UserQuest.builder()
                        .user(user)
                        .questType(entry.getKey())
                        .currentProgress(0)
                        .targetProgress(entry.getValue().getTargetProgress())
                        .status(QuestStatus.IN_PROGRESS)
                        .updatedAt(LocalDateTime.now())
                        .build())
                .collect(Collectors.toList());
                
        return userQuestRepository.saveAll(newQuests);
    }

    /**
     * [Backend] Entity -> DTO 변환 및 Title(텍스트) 주입 헬퍼 메서드
     */
    private QuestResponse mapToQuestResponse(UserQuest userQuest) {
        QuestConfig config = DEFAULT_QUESTS.getOrDefault(userQuest.getQuestType(), new QuestConfig("알 수 없는 퀘스트", 0, 0));

        return QuestResponse.builder()
                .questId(userQuest.getId())
                .questType(userQuest.getQuestType())
                .title(config.getTitle())
                .currentProgress(userQuest.getCurrentProgress())
                .targetProgress(userQuest.getTargetProgress())
                .status(userQuest.getStatus())
                .build();
    }

    /**
     * [Backend] 타 도메인 이벤트 수신 및 진행도 업데이트 로직 (핵심 트랜잭션)
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleQuestProgressEvent(QuestProgressEvent event) {
        User user = userRepository.findById(event.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. userId: " + event.getUserId()));

        QuestConfig config = DEFAULT_QUESTS.get(event.getQuestType());
        if (config == null) return;

        try {
            // [Fix - 이미지 4] rollback-only 예외 방지를 위해 INSERT IGNORE 네이티브 쿼리로 안전하게 초기화
            userQuestRepository.insertIfNotExists(user.getId(), event.getQuestType().name(), config.getTargetProgress());

            UserQuest userQuest = userQuestRepository.findByUserAndQuestTypeWithLock(user, event.getQuestType())
                    .orElseThrow(() -> new IllegalStateException("퀘스트 로드 실패"));

            if (userQuest.getStatus() != QuestStatus.IN_PROGRESS) return;

            userQuest.incrementProgress(event.getIncrementAmount());

            if (userQuest.getStatus() == QuestStatus.COMPLETED) {
                completeQuestAndReward(user, userQuest, config);
            }
        } catch (org.springframework.dao.PessimisticLockingFailureException e) {
            // [Fix - 이미지 4] 단순 Exception 삼킴 방지: 재시도가 필요한 락 타임아웃/실패 에러는 명시적으로 잡아서 로깅 및 Rethrow
            log.error("[Quest Async] 락 획득 실패. 재시도가 필요할 수 있습니다. userId: {}, questType: {}", event.getUserId(), event.getQuestType(), e);
            throw e;
        } catch (Exception e) {
            // 그 외 치명적 에러도 조용히 넘기지 않고 로그를 남긴 후 던져서 확인 가능하게 함
            log.error("[Quest Async] 퀘스트 이벤트 처리 중 치명적 오류 발생. userId: {}", event.getUserId(), e);
            throw e;
        }
    }
    /**
     * [Backend] 퀘스트 보상(XP) 지급 로직
     */
    private void completeQuestAndReward(User user, UserQuest userQuest, QuestConfig config) {
        int requiredXp = TierConfig.getRequiredXp(user.getTier());

        // streak 계산 (clearAutomatically 전에 user에서 읽기)
        LocalDate today = LocalDate.now();
        LocalDate lastLearnedDate = user.getLastLearnedDate();
        boolean shouldUpdateStreak = lastLearnedDate == null || !lastLearnedDate.isEqual(today);
        int newStreak = user.getCurrentStreak();
        int newMaxStreak = user.getMaxStreak();

        if (shouldUpdateStreak) {
            if (lastLearnedDate == null || lastLearnedDate.isBefore(today.minusDays(1))) {
                newStreak = 1; // 처음이거나 연속 끊김
            } else {
                newStreak = user.getCurrentStreak() + 1; // 어제 했으면 연속 증가
            }
            newMaxStreak = Math.max(newMaxStreak, newStreak);
        }

        // XpHistory 저장 (user managed 상태일 때 먼저)
        xpHistoryRepository.save(XpHistory.of(user, config.getXpReward(), config.getTitle()));

        // 직접 쿼리들
        userRepository.incrementUserXp(user.getId(), config.getXpReward());
        userRepository.updateTierUpgradeEligibleIfReached(user.getId(), requiredXp);
        userRepository.incrementTotalDailyQuestCompleted(user.getId());

        if (shouldUpdateStreak) {
            userRepository.updateStreak(user.getId(), newStreak, newMaxStreak, today);
        }

        userQuest.rewardQuest();
        userQuestRepository.save(userQuest);
    }
}