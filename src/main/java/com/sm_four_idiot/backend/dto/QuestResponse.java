package com.sm_four_idiot.backend.dto;

import com.sm_four_idiot.backend.domain.QuestStatus;
import com.sm_four_idiot.backend.domain.QuestType;
import lombok.Builder;
import lombok.Getter;

/**
 * [DTO] 개별 퀘스트 정보 응답 객체
 * * [Frontend] 배열 내부에 들어갈 단일 퀘스트 객체입니다.
 * - title: UI에 렌더링할 퀘스트의 한글 이름 (예: "오늘의 단어 50개 학습하기")
 * - currentProgress / targetProgress: 프로그레스 바(게이지) 렌더링에 사용되는 비율 값
 * - status: 버튼 활성화/비활성화 처리용 (IN_PROGRESS, COMPLETED, REWARDED)
 */
@Getter
@Builder
public class QuestResponse {
    private final Long questId;
    private final QuestType questType;
    private final String title;
    private final int currentProgress;
    private final int targetProgress;
    private final QuestStatus status;
}