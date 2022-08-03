package com.soongsil.swcontest.guardianProtegeControllerTest;

import com.soongsil.swcontest.common.BaseTest;
import com.soongsil.swcontest.dto.response.SignInResponseDto;
import com.soongsil.swcontest.entity.GuardianProtege;
import com.soongsil.swcontest.entity.UserInfo;
import com.soongsil.swcontest.jwt.TokenInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GetProtegesTest extends BaseTest {
    private SignInResponseDto signInResponseDto1;
    private SignInResponseDto signInResponseDto2;
    private SignInResponseDto signInResponseDto3;

    @BeforeEach
    public void setup() {
        makeUser("testuser1@naver.com", false);
        signInResponseDto1 = signInUser("testuser1@naver.com");
        makeUser("testuser2@naver.com", false);
        signInResponseDto2 = signInUser("testuser2@naver.com");
        makeUser("testuser3@naver.com", true);
        signInResponseDto3 = signInUser("testuser3@naver.com");
        UserInfo userInfo1 = userInfoRepository.findByEmail("testuser1@naver.com");
        UserInfo userInfo2 = userInfoRepository.findByEmail("testuser2@naver.com");
        UserInfo userInfo3 = userInfoRepository.findByEmail("testuser3@naver.com");
        guardianProtegeRepository.save(
                new GuardianProtege(
                        null, userInfo3, userInfo1));
        guardianProtegeRepository.save(
                new GuardianProtege(
                        null, userInfo3, userInfo2));
    }

    @Test
    @DisplayName("보호자가 등록한 환자 조회 테스트(성공)")
    public void getProtegesSuccess() throws Exception {
        ResultActions result = mockMvc.perform(get("/v1/getProtege")
                .header("Authorization", signInResponseDto3.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("protegesCount").value(2));
    }

    @Test
    @DisplayName("보호자가 등록한 환자 조회 테스트(실패) - 없는 유저일 때")
    public void getProtegesFailUserNotFound() throws Exception {
        TokenInfo tokenInfo = jwtTokenProvider.createJwtAccessToken("test@naver.com");
        ResultActions result = mockMvc.perform(get("/v1/getProtege")
                .header("Authorization", tokenInfo.getToken())
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("유저서비스 오류 4번"));
    }

    @Test
    @DisplayName("환자를 등록한 보호자 조회 테스트(실패) - 환자가 사용할 때")
    public void getProtegesFailUserIsGuardian() throws Exception {
        ResultActions result = mockMvc.perform(get("/v1/getProtege")
                .header("Authorization", signInResponseDto2.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("보호자-피보호자 오류 1번"));
    }
}
