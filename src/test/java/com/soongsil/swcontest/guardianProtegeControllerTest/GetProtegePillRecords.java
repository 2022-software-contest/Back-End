package com.soongsil.swcontest.guardianProtegeControllerTest;

import com.soongsil.swcontest.common.BaseTest;
import com.soongsil.swcontest.dto.request.GetProtegePillRecordsRequestDto;
import com.soongsil.swcontest.dto.response.SignInResponseDto;
import com.soongsil.swcontest.entity.GuardianProtege;
import com.soongsil.swcontest.entity.Pill;
import com.soongsil.swcontest.entity.UserInfo;
import com.soongsil.swcontest.jwt.TokenInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GetProtegePillRecords extends BaseTest {
    private SignInResponseDto signInResponseDto1;
    private SignInResponseDto signInResponseDto2;
    private SignInResponseDto signInResponseDto3;

    @BeforeEach
    public void setup() {
        makeUser("testuser1@naver.com", false);
        signInResponseDto1 = signInUser("testuser1@naver.com");
        makeUser("testuser2@naver.com", true);
        signInResponseDto2 = signInUser("testuser2@naver.com");
        makeUser("testuser3@naver.com", true);
        signInResponseDto3 = signInUser("testuser3@naver.com");
        UserInfo userInfo1 = userInfoRepository.findByEmail("testuser1@naver.com");
        UserInfo userInfo2 = userInfoRepository.findByEmail("testuser2@naver.com");
        guardianProtegeRepository.save(
                new GuardianProtege(
                        null, userInfo2, userInfo1));
        pillRepository.save(
                new Pill(null, userInfo1, "zolpidem", "drug", LocalDateTime.now())
        );
        pillRepository.save(
                new Pill(null, userInfo1, "ibuprophen", "drug", LocalDateTime.now())
        );
    }

    @Test
    @DisplayName("보호자가 등록한 환자의 약 정보 가져오기(성공)")
    public void getProtegePillRecordsSuccess() throws Exception {
        GetProtegePillRecordsRequestDto getProtegePillRecordsRequestDto
                = new GetProtegePillRecordsRequestDto("testuser1@naver.com");
        ResultActions result = mockMvc.perform(post("/v1/getProtegePillRecord")
                .header("Authorization", signInResponseDto2.getAccessToken())
                .content(objectMapper.writeValueAsString(getProtegePillRecordsRequestDto))
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("pillCount").value(2))
                .andExpect(jsonPath("pills[0].pillName").value("zolpidem"))
                .andExpect(jsonPath("pills[1].pillName").value("ibuprophen"));
    }

    @Test
    @DisplayName("보호자가 등록한 환자의 약 정보 가져오기(실패) - 없는 유저일 때")
    public void getProtegePillRecordsFailUserNotFound() throws Exception {
        TokenInfo tokenInfo = jwtTokenProvider.createJwtAccessToken("test@naver.com");
        GetProtegePillRecordsRequestDto getProtegePillRecordsRequestDto
                = new GetProtegePillRecordsRequestDto("testuser1@naver.com");

        ResultActions result = mockMvc.perform(post("/v1/getProtegePillRecord")
                .header("Authorization", tokenInfo.getToken())
                .content(objectMapper.writeValueAsString(getProtegePillRecordsRequestDto))
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("유저서비스 오류 4번"));
    }

    @Test
    @DisplayName("보호자가 등록한 환자의 약 정보 가져오기(실패) - 사용자가 환자일 때")
    public void getProtegePillRecordsFailUserIsProtege() throws Exception {
        GetProtegePillRecordsRequestDto getProtegePillRecordsRequestDto
                = new GetProtegePillRecordsRequestDto("testuser1@naver.com");

        ResultActions result = mockMvc.perform(post("/v1/getProtegePillRecord")
                .header("Authorization", signInResponseDto1.getAccessToken())
                .content(objectMapper.writeValueAsString(getProtegePillRecordsRequestDto))
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("보호자-피보호자 오류 1번"));
    }

    @Test
    @DisplayName("보호자가 등록한 환자의 약 정보 가져오기(실패) - 환자가 없을 때")
    public void getProtegePillRecordsFailProtegeNotFound() throws Exception {
        GetProtegePillRecordsRequestDto getProtegePillRecordsRequestDto
                = new GetProtegePillRecordsRequestDto("test@naver.com");

        ResultActions result = mockMvc.perform(post("/v1/getProtegePillRecord")
                .header("Authorization", signInResponseDto2.getAccessToken())
                .content(objectMapper.writeValueAsString(getProtegePillRecordsRequestDto))
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("유저서비스 오류 4번"));
    }

    @Test
    @DisplayName("보호자가 등록한 환자의 약 정보 가져오기(실패) - 사용자가 피보호자를 등록하지 않았을 때")
    public void getProtegePillRecordsFailUserHasNotProtege() throws Exception {
        GetProtegePillRecordsRequestDto getProtegePillRecordsRequestDto
                = new GetProtegePillRecordsRequestDto("testuser1@naver.com");

        ResultActions result = mockMvc.perform(post("/v1/getProtegePillRecord")
                .header("Authorization", signInResponseDto3.getAccessToken())
                .content(objectMapper.writeValueAsString(getProtegePillRecordsRequestDto))
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("보호자-피보호자 오류 6번"));
    }
}
