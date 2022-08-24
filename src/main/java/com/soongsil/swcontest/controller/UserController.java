package com.soongsil.swcontest.controller;

import com.soongsil.swcontest.dto.request.SignInRequestDto;
import com.soongsil.swcontest.dto.request.SignUpRequestDto;
import com.soongsil.swcontest.dto.request.ChangePasswordRequestDto;
import com.soongsil.swcontest.dto.response.ReissueResponseDto;
import com.soongsil.swcontest.dto.response.SignInResponseDto;
import com.soongsil.swcontest.dto.response.SignUpResponseDto;
import com.soongsil.swcontest.entity.UserInfo;
import com.soongsil.swcontest.security.AuthInfo;
import com.soongsil.swcontest.security.Authenticated;
import com.soongsil.swcontest.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

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
        return userService.signUp(signUpRequestDto.getEmail(),
                signUpRequestDto.getPassword(),
                signUpRequestDto.getUsername(),
                signUpRequestDto.getPhoneNumber(),
                signUpRequestDto.getIsGuardian(),
                signUpRequestDto.getRole());
    }

    @ApiOperation("로그인")
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

    @ApiOperation("비밀번호 변경")
    @PostMapping("/v1/changePassword")
    public void changePassword(@Authenticated AuthInfo authInfo, @RequestBody @Validated ChangePasswordRequestDto changePasswordRequestDto) {
        userService.changePassword(authInfo.getEmail(), changePasswordRequestDto.getOldPassword(), changePasswordRequestDto.getNewPassword());
    }

    @ApiOperation("회원탈퇴 액세스토큰을 사용할 것")
    @PostMapping("/v1/withdraw")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void withdraw(@Authenticated AuthInfo authInfo) {
        userService.withdraw(authInfo.getToken(), authInfo.getEmail());
    }

    @ApiOperation("회원 조회 개발용도")
    @GetMapping("/v1/display")
    public List<UserInfo> display() {
        return userService.display();
    }
}
