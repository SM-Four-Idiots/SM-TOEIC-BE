package com.sm_four_idiot.backend.service;

import com.sm_four_idiot.backend.domain.User;
import com.sm_four_idiot.backend.dto.RankingResponse;
import com.sm_four_idiot.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * 랭킹 보드 비즈니스 로직
 */
@Service
@RequiredArgsConstructor
public class RankingService {

    private final UserRepository userRepository;

    /**
     * 전체 랭킹 조회
     * - 티어 내림차순, 동일 티어 내 XP 내림차순 정렬
     * - 본인 여부 (isMe) 표시
     * @param email JWT에서 추출한 이메일
     * @return 내 순위 + 전체 랭킹 목록
     */
    @Transactional(readOnly = true)
    public RankingResponse getRanking(String email) {
        User me = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다"));

        List<User> users = userRepository.findAllOrderByTierAndXp();

        List<RankingResponse.RankingEntry> rankings = new ArrayList<>();
        RankingResponse.RankingEntry myRank = null;

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            boolean isMe = user.getId().equals(me.getId());

            RankingResponse.RankingEntry entry = RankingResponse.RankingEntry.builder()
                    .rank(i + 1)
                    .nickname(user.getNickname())
                    .tier(user.getTier().name())
                    .xp(user.getXp())
                    .isMe(isMe)
                    .build();

            rankings.add(entry);

            if (isMe) {
                myRank = entry;
            }
        }

        return RankingResponse.builder()
                .myRank(myRank)
                .rankings(rankings)
                .build();
    }
}