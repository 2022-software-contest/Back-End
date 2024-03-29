package com.soongsil.swcontest.pushTokenControllerTest;

import com.soongsil.swcontest.common.BaseTest;
import com.soongsil.swcontest.dto.request.RegisterTokenRequestDto;
import com.soongsil.swcontest.dto.response.SignInResponseDto;
import com.soongsil.swcontest.jwt.TokenInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RegisterPushTokenTest extends BaseTest {
    private SignInResponseDto signInResponseDto1;

    @BeforeEach
    public void setup() {
        makeUser("testuser1@naver.com", false);
        signInResponseDto1 = signInUser("testuser1@naver.com");
    }

    @Test
    @DisplayName("토큰 등록 테스트(성공)")
    public void registerPushTokenSuccess() throws Exception {
        RegisterTokenRequestDto registerTokenRequestDto = new RegisterTokenRequestDto("test token");

        ResultActions result = mockMvc.perform(post("/v1/registerToken")
                .header("Authorization", signInResponseDto1.getAccessToken())
                .content(objectMapper.writeValueAsString(registerTokenRequestDto))
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("token").value("test token"));
    }

    @Test
    @DisplayName("토큰 등록 테스트(실패) - 없는 유저 일 때")
    public void registerPushTokenFailUserNotFound() throws Exception {
        RegisterTokenRequestDto registerTokenRequestDto = new RegisterTokenRequestDto("test token");
        TokenInfo tokenInfo = jwtTokenProvider.createJwtAccessToken("test@naver.com");

        ResultActions result = mockMvc.perform(post("/v1/registerToken")
                .header("Authorization", tokenInfo.getToken())
                .content(objectMapper.writeValueAsString(registerTokenRequestDto))
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("유저서비스 오류 4번"));
    }
}
