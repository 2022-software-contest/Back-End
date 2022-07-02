package com.soongsil.swcontest.controller;

import com.soongsil.swcontest.dto.request.SignInRequestDto;
import com.soongsil.swcontest.dto.request.SignUpRequestDto;
import com.soongsil.swcontest.dto.response.ReissueResponseDto;
import com.soongsil.swcontest.dto.response.SignInResponseDto;
import com.soongsil.swcontest.dto.response.SignUpResponseDto;
import com.soongsil.swcontest.security.AuthInfo;
import com.soongsil.swcontest.security.Authenticated;
import com.soongsil.swcontest.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @ApiOperation("서버 시간 제공")
    @GetMapping("/")
    public String time() {
        return LocalDateTime.now().toString();
    }

    @ApiOperation("회원가입")
    @PostMapping("/v1/signUp")
    public SignUpResponseDto signUp(@Valid @RequestBody SignUpRequestDto signUpRequestDto) {
        return userService.signUp(signUpRequestDto.getEmail(), signUpRequestDto.getPassword(), signUpRequestDto.getUsername(), signUpRequestDto.getRole());
    }

    @ApiOperation("로그인 a")
    @PostMapping("/v1/signIn")
    public SignInResponseDto signIn(@Valid @RequestBody SignInRequestDto signInRequestDto) {
        return userService.signIn(signInRequestDto.getEmail(), signInRequestDto.getPassword());
    }

    @ApiOperation("토큰 재발급 리프레시토큰을 사용할 것")
    @PostMapping("/v1/reissue")
    public ReissueResponseDto reissue(@Authenticated AuthInfo authInfo) {
        return userService.reissue(authInfo.getToken(), authInfo.getEmail());
    }

    @ApiOperation("로그아웃 액세스토큰을 사용할 것")
    @PostMapping("/v1/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@Authenticated AuthInfo authInfo) {
        userService.logout(authInfo.getToken(), authInfo.getEmail());
    }

    @ApiOperation("회원탈퇴 액세스토큰을 사용할 것")
    @PostMapping("/v1/withdraw")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void withdraw(@Authenticated AuthInfo authInfo) {
        userService.withdraw(authInfo.getToken(), authInfo.getEmail());
    }
}
