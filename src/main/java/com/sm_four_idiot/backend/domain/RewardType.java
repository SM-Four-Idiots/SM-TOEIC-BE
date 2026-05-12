package com.sm_four_idiot.backend.domain;

/**
 * [Front-end] 서버에 보상을 요청할 때 '어떤 행동에 대한 보상인지'를 명시하는 상수값 목록입니다.
 * 클라이언트에서는 문자열("QUEST_COMPLETION", "WORD_TEST_PERFECT") 그대로 전송해야 합니다.
 *
 * [Back-end] Type Safe한 비즈니스 로직 처리를 위해 사용되며 DB에는 문자열 형태로 저장됩니다.
 */
public enum RewardType {
    QUEST_COMPLETION,
    WORD_TEST_PERFECT
}