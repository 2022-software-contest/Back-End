package com.soongsil.swcontest.guardianProtegeController;

import com.soongsil.swcontest.common.BaseTest;
import com.soongsil.swcontest.dto.request.AddProtegeRequestDto;
import com.soongsil.swcontest.dto.response.SignInResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AddProtegeTest extends BaseTest {
    private SignInResponseDto signInResponseDto1;
    private SignInResponseDto signInResponseDto2;

    @BeforeEach
    public void setup() {
        makeUser("testuser1@naver.com", false);
        signInResponseDto1 = signInUser("testuser1@naver.com");
        makeUser("testuser2@naver.com", true);
        signInResponseDto2 = signInUser("testuser2@naver.com");
    }

    @Test
    @DisplayName("피보호자 추가 테스트(성공)")
    public void addProtegeSuccess() throws Exception {
        AddProtegeRequestDto addProtegeRequestDto = new AddProtegeRequestDto("testuser1@naver.com", phoneNumber);

        ResultActions result = mockMvc.perform(post("/v1/addProtege")
                .header("Authorization", signInResponseDto2.getAccessToken())
                .content(objectMapper.writeValueAsString(addProtegeRequestDto))
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("guardianEmail").value(signInResponseDto2.getEmail()))
                .andExpect(jsonPath("protegeEmail").value(signInResponseDto1.getEmail()));
    }

    @Test
    @DisplayName("피보호자 추가 테스트(실패)-보호자를 찾을 수 없을 때")
    public void addProtegeFailBecauseGuardianNotFound() throws Exception {
        String accessToken = jwtTokenProvider.createJwtAccessToken("email@email.com").getToken();
        AddProtegeRequestDto addProtegeRequestDto = new AddProtegeRequestDto("testuser1@naver.com", phoneNumber);

        ResultActions result = mockMvc.perform(post("/v1/addProtege")
                .header("Authorization", accessToken)
                .content(objectMapper.writeValueAsString(addProtegeRequestDto))
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("errorCode").value("유저서비스 오류 4번"));
    }

    @Test
    @DisplayName("피보호자 추가 테스트(실패)-사용자가 보호자가 아닐 때")
    public void addProtegeFailBecauseUserIsNotGuardian() throws Exception {
        AddProtegeRequestDto addProtegeRequestDto = new AddProtegeRequestDto("testuser1@naver.com", phoneNumber);

        ResultActions result = mockMvc.perform(post("/v1/addProtege")
                .header("Authorization", signInResponseDto1.getAccessToken())
                .content(objectMapper.writeValueAsString(addProtegeRequestDto))
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("errorCode").value("피보호자 추가 오류 1번"));
    }

    @Test
    @DisplayName("피보호자 추가 테스트(실패)-피보호자를 찾을 수 없을 때")
    public void addProtegeFailBecauseNotFoundProtege() throws Exception {
        AddProtegeRequestDto addProtegeRequestDto = new AddProtegeRequestDto("test@naver.com", phoneNumber);

        ResultActions result = mockMvc.perform(post("/v1/addProtege")
                .header("Authorization", signInResponseDto2.getAccessToken())
                .content(objectMapper.writeValueAsString(addProtegeRequestDto))
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("errorCode").value("유저서비스 오류 4번"));
    }

    @Test
    @DisplayName("피보호자 추가 테스트(실패)-피보호자의 전화번호가 일치하지 않을 때")
    public void addProtegeFailBecauseProtegePhoneNumberNotEqual() throws Exception {
        AddProtegeRequestDto addProtegeRequestDto = new AddProtegeRequestDto("testuser1@naver.com", "1111");

        ResultActions result = mockMvc.perform(post("/v1/addProtege")
                .header("Authorization", signInResponseDto2.getAccessToken())
                .content(objectMapper.writeValueAsString(addProtegeRequestDto))
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("errorCode").value("피보호자 추가 오류 2번"));
    }

    @Test
    @DisplayName("피보호자 추가 테스트(실패)-등록대상이 보호자일 때")
    public void addProtegeFailBecauseTargetIsGuardian() throws Exception {
        AddProtegeRequestDto addProtegeRequestDto = new AddProtegeRequestDto("testuser2@naver.com", phoneNumber);

        ResultActions result = mockMvc.perform(post("/v1/addProtege")
                .header("Authorization", signInResponseDto2.getAccessToken())
                .content(objectMapper.writeValueAsString(addProtegeRequestDto))
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("errorCode").value("피보호자 추가 오류 3번"));
    }

    @Test
    @DisplayName("피보호자 추가 테스트(실패)-피보호자가 이미 등록되어 있을 때")
    public void addProtegeFailBecauseProtegeAlreadyExists() throws Exception {
        addProtege("testuser2@naver.com", "testuser1@naver.com", phoneNumber);
        AddProtegeRequestDto addProtegeRequestDto = new AddProtegeRequestDto("testuser1@naver.com", phoneNumber);

        ResultActions result = mockMvc.perform(post("/v1/addProtege")
                .header("Authorization", signInResponseDto2.getAccessToken())
                .content(objectMapper.writeValueAsString(addProtegeRequestDto))
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("errorCode").value("피보호자 추가 오류 4번"));
    }
}
