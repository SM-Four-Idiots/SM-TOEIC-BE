package com.sm_four_idiot.backend.domain;

/**
 * [Domain Enum] 퀘스트의 종류를 정의하는 열거형
 * 이벤트 발생 시 어떤 퀘스트의 진행도를 올릴지 식별하는 고유 키 역할을 합니다.
 */
public enum QuestType {
    WORD_LEARN,    // 단어 학습 완료 퀘스트
    WORDLE_PLAY,   // 워들 게임 플레이 퀘스트
    TEST_COMPLETE  // 테스트 응시 완료 퀘스트
}