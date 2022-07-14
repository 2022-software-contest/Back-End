package com.soongsil.swcontest.service;

import com.soongsil.swcontest.dto.response.ReissueResponseDto;
import com.soongsil.swcontest.dto.response.SignInResponseDto;
import com.soongsil.swcontest.dto.response.SignUpResponseDto;
import com.soongsil.swcontest.entity.UserInfo;
import com.soongsil.swcontest.enums.RoleType;
import com.soongsil.swcontest.exception.userServiceException.*;
import com.soongsil.swcontest.jwt.JwtTokenProvider;
import com.soongsil.swcontest.jwt.TokenInfo;
import com.soongsil.swcontest.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserInfoRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public SignUpResponseDto signUp(String email, String password, String username, RoleType role) {
        if(userRepository.existsByEmail(email)) {
            throw new ExistsEmailException("이미 가입한 이메일 입니다.");
        }
        userRepository.save(
                new UserInfo(null, email, bCryptPasswordEncoder.encode(password), username, RoleType.USER, null)
        );
        return new SignUpResponseDto(email, username, role);
    }

    @Transactional
    public SignInResponseDto signIn(String email, String password) {
        UserInfo user = userRepository.findByEmail(email);
        if (user==null) {
            throw new UserNotFoundException("사용자를 찾을 수 없습니다.");
        }
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new PasswordIncorrectException("비밀번호가 맞지 않습니다.");
        }

        TokenInfo accessTokenDto = jwtTokenProvider.createJwtAccessToken(email);
        TokenInfo refreshTokenDto = jwtTokenProvider.createJwtRefreshToken(email);
        user.updateRefreshToken(refreshTokenDto.getToken());
        return new SignInResponseDto(user.getEmail(), user.getUsername(), user.getRole(), accessTokenDto.getToken(), refreshTokenDto.getToken());
    }

    @Transactional
    public ReissueResponseDto reissue(String refreshToken, String email) {
        UserInfo user = userRepository.findByEmail(email);
        if (user==null) {
            throw new UserNotFoundException("사용자를 찾을 수 없습니다.");
        }
        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new RefreshTokenNotFoundException("리프레시 토큰을 찾을 수 없습니다. 다시 로그인 하세요.");
        }

        TokenInfo accessTokenDto = jwtTokenProvider.createJwtAccessToken(email);
        TokenInfo refreshTokenDto = jwtTokenProvider.createJwtRefreshToken(email);
        user.updateRefreshToken(refreshTokenDto.getToken());
        return new ReissueResponseDto(email, accessTokenDto.getToken(), refreshTokenDto.getToken());
    }

    @Transactional
    public void logout(String accessToken, String email) {
        UserInfo user = userRepository.findByEmail(email);
        if (user==null) {
            throw new UserNotFoundException("사용자를 찾을 수 없습니다.");
        }

        if (accessToken.equals(user.getRefreshToken())) {
            throw new RefreshTokenImproperUseException("로그아웃 할때 액세스토큰을 사용해 주세요. 액세스토큰이 없다면 리프레시 토큰으로 재발급해주세요.");
        }

        if (user.getRefreshToken()==null) {
            throw new AlreadyLogoutException("이미 로그아웃된 사용자 입니다. 다시 로그인 해주세요.");
        }
        user.updateRefreshToken(null);
    }

    @Transactional
    public void withdraw(String accessToken, String email) {
        UserInfo user = userRepository.findByEmail(email);
        if (user==null) {
            throw new UserNotFoundException("사용자를 찾을 수 없습니다.");
        }

        if (accessToken.equals(user.getRefreshToken())) {
            throw new RefreshTokenImproperUseException("회원탈퇴 할때 액세스토큰을 사용해 주세요. 액세스토큰이 없다면 리프레시 토큰으로 재발급해주세요.");
        }
        userRepository.delete(user);
    }

    @Transactional
    public List<UserInfo> display() {
        return userRepository.findAll();
    }
}
