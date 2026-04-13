package com.sm_four_idiot.backend.service;

import com.sm_four_idiot.backend.domain.User;
import com.sm_four_idiot.backend.dto.UserInfoResponse;
import com.sm_four_idiot.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * 유저 관련 비즈니스 로직
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * 유저 정보 조회
     * @param email JWT에서 추출한 이메일
     * @return 이메일, 닉네임
     */
    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfo(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다"));
        return new UserInfoResponse(user.getNickname());
    }
}