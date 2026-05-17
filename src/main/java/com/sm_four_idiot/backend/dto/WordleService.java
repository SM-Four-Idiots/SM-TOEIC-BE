package com.sm_four_idiot.backend.service;

import com.sm_four_idiot.backend.domain.QuestType;
import com.sm_four_idiot.backend.domain.User;
import com.sm_four_idiot.backend.domain.Word;
import com.sm_four_idiot.backend.dto.WordleStartResponse;
import com.sm_four_idiot.backend.event.QuestProgressEvent;
import com.sm_four_idiot.backend.repository.UserRepository;
import com.sm_four_idiot.backend.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * 워들 게임 비즈니스 로직
 */
@Service
@RequiredArgsConstructor
public class WordleService {

    private final UserRepository userRepository;
    private final WordRepository wordRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 워들 게임 시작
     * - 오늘 이미 플레이했으면 alreadyPlayed=true 반환
     * - 5글자 단어 중 랜덤으로 1개 선택해서 반환
     * @param email JWT에서 추출한 이메일
     * @return 게임 시작 응답 (wordId, answer, length, alreadyPlayed)
     */
    @Transactional(readOnly = true)
    public WordleStartResponse startGame(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다"));

        LocalDate today = LocalDate.now();
        boolean alreadyPlayed = user.getLastWordleDate() != null
                && user.getLastWordleDate().isEqual(today);

        List<Word> fiveLetterWords = wordRepository.findByVocaLength(5);
        if (fiveLetterWords.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "워들에 사용할 단어가 없습니다");
        }
        Collections.shuffle(fiveLetterWords);
        Word word = fiveLetterWords.get(0);

        return WordleStartResponse.builder()
                .wordId(word.getId())
                .answer(word.getVoca())
                .length(5)
                .alreadyPlayed(alreadyPlayed)
                .build();
    }

    /**
     * 워들 게임 완료 처리
     * - 오늘 첫 플레이면 lastWordleDate 업데이트 + 퀘스트 완료 이벤트 발행
     * @param email JWT에서 추출한 이메일
     */
    @Transactional
    public void completeGame(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다"));

        LocalDate today = LocalDate.now();

        // 오늘 이미 플레이했으면 퀘스트 중복 완료 방지
        if (user.getLastWordleDate() != null
                && user.getLastWordleDate().isEqual(today)) {
            return;
        }

        userRepository.updateLastWordleDate(user.getId(), today);

        // 퀘스트 완료 이벤트 발행
        eventPublisher.publishEvent(
                new QuestProgressEvent(this, user.getId(), QuestType.WORDLE_PLAY, 1));
    }
}