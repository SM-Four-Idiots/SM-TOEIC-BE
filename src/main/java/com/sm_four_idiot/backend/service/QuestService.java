package com.sm_four_idiot.backend.service;

import com.sm_four_idiot.backend.domain.*;
import com.sm_four_idiot.backend.dto.QuestDashboardResponse;
import com.sm_four_idiot.backend.dto.QuestResponse;
import com.sm_four_idiot.backend.event.QuestProgressEvent;
import com.sm_four_idiot.backend.repository.UserQuestRepository;
import com.sm_four_idiot.backend.repository.UserRepository;
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
     * * 1. @Async: 메인 비즈니스(예: 테스트 응시) 스레드를 막지 않고 백그라운드 워커 스레드에서 비동기 실행됩니다.
     * 2. TransactionPhase.AFTER_COMMIT: 메인 비즈니스가 DB에 완전히 Commit 성공했을 때만 이 퀘스트 로직이 발동합니다.
     * 3. Propagation.REQUIRES_NEW: 퀘스트 업데이트 중 에러(DB 데드락 등)가 발생해도, 이미 처리된 메인 비즈니스는 Rollback 시키지 않도록 트랜잭션을 완전히 분리합니다.
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
            // [Fix 3] 비관적 락을 적용하여 갱신 손실 방지 (데이터베이스 Row 수준의 쓰기 잠금)
            UserQuest userQuest = userQuestRepository.findByUserAndQuestTypeWithLock(user, event.getQuestType())
                    .orElseGet(() -> {
                        try {
                            // 대시보드를 방문하지 않아 퀘스트가 없는 상태에서 이벤트가 터졌을 때 즉시 생성
                            return userQuestRepository.save(UserQuest.builder()
                                    .user(user)
                                    .questType(event.getQuestType())
                                    .currentProgress(0)
                                    .targetProgress(config.getTargetProgress())
                                    .status(QuestStatus.IN_PROGRESS)
                                    .updatedAt(LocalDateTime.now())
                                    .build());
                        } catch (DataIntegrityViolationException e) {
                            // 여기도 퀘스트가 없는 상태에서 이벤트가 동시 다발적으로 터질 경우의 방어 로직 (DB 제약조건 에러 Catch 후 재조회)
                            return userQuestRepository.findByUserAndQuestTypeWithLock(user, event.getQuestType())
                                    .orElseThrow(); 
                        }
                    });

            if (userQuest.getStatus() != QuestStatus.IN_PROGRESS) return;

            // 로드된 퀘스트의 진행도를 도메인 로직을 통해 증가
            userQuest.incrementProgress(event.getIncrementAmount());

            // 달성되었다면 보상 로직 호출
            if (userQuest.getStatus() == QuestStatus.COMPLETED) {
                completeQuestAndReward(user, userQuest, config);
            }
        } catch (Exception e) {
            log.error("퀘스트 이벤트 처리 중 오류 발생. userId: {}", event.getUserId(), e);
        }
    }

    /**
     * [Backend] 퀘스트 보상(XP) 지급 로직
     */
    private void completeQuestAndReward(User user, UserQuest userQuest, QuestConfig config) {
        // [Fix 2 적용] 객체에 더하는 대신 DB 레벨의 Update 쿼리 실행으로 동시성 문제(XP 증발) 원천 차단
        // 영속성 컨텍스트(clearAutomatically) 문제를 해결했으므로 안전하게 동작함
        userRepository.incrementUserXp(user.getId(), config.getXpReward());
        
        userQuest.rewardQuest();
        userQuestRepository.save(userQuest); 
    }
}