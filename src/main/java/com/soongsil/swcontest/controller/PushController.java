package com.soongsil.swcontest.controller;

import com.soongsil.swcontest.dto.request.RegisterTokenRequestDto;
import com.soongsil.swcontest.dto.response.RegisterTokenResponseDto;
import com.soongsil.swcontest.security.AuthInfo;
import com.soongsil.swcontest.security.Authenticated;
import com.soongsil.swcontest.service.PushTokenService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class PushController {
    private final PushTokenService pushTokenService;

    @PostMapping("/v1/registerToken")
    @ApiOperation("안드로이드 FCM토큰 등록")
    public RegisterTokenResponseDto registerToken(@Authenticated AuthInfo authInfo, @Valid @RequestBody RegisterTokenRequestDto registerTokenRequestDto) {
            return pushTokenService.registerPushToken(authInfo.getEmail(), registerTokenRequestDto.getToken());
    }

    @PostMapping("/v1/deleteToken")
    @ApiOperation("안드로이드 FCM토큰 삭제(로그아웃시 사용)")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteToken(@Authenticated AuthInfo authInfo) {
        pushTokenService.deletePushToken(authInfo.getEmail());
    }
}
