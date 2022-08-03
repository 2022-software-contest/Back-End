package com.soongsil.swcontest.service;

import com.soongsil.swcontest.dto.response.RegisterTokenResponseDto;
import com.soongsil.swcontest.entity.PushToken;
import com.soongsil.swcontest.entity.UserInfo;
import com.soongsil.swcontest.exception.userServiceException.UserNotFoundException;
import com.soongsil.swcontest.repository.PushTokenRepository;
import com.soongsil.swcontest.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PushTokenService {
    private final UserInfoRepository userInfoRepository;
    private final PushTokenRepository pushTokenRepository;

    public RegisterTokenResponseDto registerPushToken(String email, String token) {
        UserInfo userInfo = userInfoRepository.findByEmail(email);
        if (userInfo == null) {
            throw new UserNotFoundException("FCM토큰을 등록하기위한 유저가 없습니다.");
        }

        PushToken pushToken = pushTokenRepository.findByUserInfo(userInfo);
        if(pushToken==null) {
            pushTokenRepository.save(new PushToken(null, token, userInfo));
        }
        else {
            pushToken.setToken(token);
        }
        return new RegisterTokenResponseDto(token);
    }
}
