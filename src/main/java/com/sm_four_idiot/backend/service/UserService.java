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
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("해당 이메일을 가진 사용자를 찾을 수 없습니다."));
        // RuntimeException 대신 프로젝트에서 사용하는 Custom Exception이 있다면 그것을 사용하세요.
    }

    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfo(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다"));
        return new UserInfoResponse(user.getNickname());
    }
}