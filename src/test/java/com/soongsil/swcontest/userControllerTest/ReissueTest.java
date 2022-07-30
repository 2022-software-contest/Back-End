package com.soongsil.swcontest.userControllerTest;

import com.soongsil.swcontest.common.BaseTest;
import com.soongsil.swcontest.dto.response.SignInResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ReissueTest extends BaseTest {
    private SignInResponseDto signInResponseDto;
    @BeforeEach
    public void setup() {
        makeUser("testuser1@naver.com");
        signInResponseDto = signInUser("testuser1@naver.com");
    }

    @Test
    @DisplayName("토큰 재발급 테스트(성공)")
    public void reissueTestSuccess() throws Exception {
        // given
        // when
        ResultActions result = mockMvc.perform(post("/v1/reissue")
                .header("Authorization",signInResponseDto.getRefreshToken())
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("email").value(email))
                .andExpect(jsonPath("accessToken").exists())
                .andExpect(jsonPath("refreshToken").exists());
    }

    @Test
    @DisplayName("토큰 재발급 테스트(실패)-없는 유저의 토큰일 때")
    public void reissueFailBecauseInvalidUser() throws Exception {
        // given
        String accessToken = jwtTokenProvider.createJwtAccessToken("email@email.com").getToken();
        // when
        ResultActions result = mockMvc.perform(post("/v1/reissue")
                .header("Authorization",accessToken)
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("errorCode").value("유저서비스 오류 4번"));
    }

    @Test
    @DisplayName("토큰 재발급 테스트(실패)-액세스 토큰일 때")
    public void reissueFailBecauseInvalidEmailFormat() throws Exception {
        // given
        // when
        ResultActions result = mockMvc.perform(post("/v1/reissue")
                .header("Authorization",signInResponseDto.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("errorCode").value("유저서비스 오류 3번"));
    }
}
