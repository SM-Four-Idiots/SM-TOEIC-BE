package com.sm_four_idiot.backend.service;

import com.sm_four_idiot.backend.domain.User;
import com.sm_four_idiot.backend.domain.Word;
import com.sm_four_idiot.backend.dto.WrongWordListResponse;
import com.sm_four_idiot.backend.dto.WrongWordResponse;
import com.sm_four_idiot.backend.repository.UserRepository;
import com.sm_four_idiot.backend.repository.WordRepository;
import com.sm_four_idiot.backend.repository.WrongWordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 오답 노트 비즈니스 로직
 */
@Service
@RequiredArgsConstructor
public class WrongWordService {

    private final WrongWordRepository wrongWordRepository;
    private final UserRepository userRepository;
    private final WordRepository wordRepository;

    /**
     * 오답 노트 목록 조회
     * - 틀린 횟수 내림차순 정렬
     * @param email JWT에서 추출한 이메일
     * @return 오답 노트 목록
     */
    @Transactional(readOnly = true)
    public WrongWordListResponse getWrongWords(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다"));

        List<WrongWordResponse> words = wrongWordRepository
                .findByUserOrderByWrongCountDesc(user)
                .stream()
                .map(WrongWordResponse::new)
                .collect(Collectors.toList());

        return WrongWordListResponse.builder()
                .totalCount(words.size())
                .words(words)
                .build();
    }

    /**
     * 오답 노트 전체 삭제
     * @param email JWT에서 추출한 이메일
     */
    @Transactional
    public void deleteAllWrongWords(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다"));

        wrongWordRepository.deleteAllByUser(user);
    }

    /**
     * 오답 노트 단어 하나 삭제 (학습 완료)
     * @param email JWT에서 추출한 이메일
     * @param wordId 삭제할 단어 ID
     */
    @Transactional
    public void deleteWrongWord(String email, Long wordId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다"));

        Word word = wordRepository.findById(wordId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "단어를 찾을 수 없습니다"));

        WrongWord wrongWord = wrongWordRepository.findByUserAndWord(user, word)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "오답 노트에 없는 단어입니다"));

        wrongWordRepository.delete(wrongWord);
    }
}