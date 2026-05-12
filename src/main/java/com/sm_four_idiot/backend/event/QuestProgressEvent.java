package com.sm_four_idiot.backend.event;

import com.sm_four_idiot.backend.domain.QuestType;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * [Event] 타 도메인(학습, 워들, 테스트)에서 발행하는 이벤트 객체
 * * [Backend] 의존성 분리를 위해 Spring ApplicationEvent를 상속받습니다.
 * - 타 서비스 로직 처리 완료 직후 이 이벤트를 발행(Publish)하면 QuestService가 수신(Listen)하여 비동기로 처리합니다.
 */
@Getter
public class QuestProgressEvent extends ApplicationEvent {
    private final Long userId;
    private final QuestType questType;
    private final int incrementAmount;

    public QuestProgressEvent(Object source, Long userId, QuestType questType, int incrementAmount) {
        super(source);
        this.userId = userId;
        this.questType = questType;
        this.incrementAmount = incrementAmount;
    }
}