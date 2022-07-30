package com.soongsil.swcontest.guardianProtegeController;

import com.soongsil.swcontest.common.BaseTest;
import com.soongsil.swcontest.dto.request.DeleteProtegeRequestDto;
import com.soongsil.swcontest.dto.response.SignInResponseDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class DeleteProtegeTest extends BaseTest {
    private SignInResponseDto signInResponseDto1;
    private SignInResponseDto signInResponseDto2;

    @BeforeEach
    public void setup() {
        makeUser("testuser1@naver.com", false);
        signInResponseDto1 = signInUser("testuser1@naver.com");
        makeUser("testuser2@naver.com", true);
        signInResponseDto2 = signInUser("testuser2@naver.com");
        addProtege("testuser2@naver.com", "testuser1@naver.com", phoneNumber);
    }

    @Test
    public void deleteProtegeSuccess() throws Exception {
        DeleteProtegeRequestDto deleteProtegeRequestDto = new DeleteProtegeRequestDto("testuser1@naver.com");

        ResultActions result = mockMvc.perform(post("/v1/deleteProtege")
                .header("Authorization", signInResponseDto2.getAccessToken())
                .content(objectMapper.writeValueAsString(deleteProtegeRequestDto))
                .contentType(MediaType.APPLICATION_JSON));

        Assertions.assertThat(guardianProtegeRepository.findAll().size()).isEqualTo(0);
        result.andExpect(status().isNoContent());
    }

    @Test
    public void deleteProtegeFailBecauseGuardianNotFound() throws Exception {
        String accessToken = jwtTokenProvider.createJwtAccessToken("email@email.com").getToken();
        DeleteProtegeRequestDto deleteProtegeRequestDto = new DeleteProtegeRequestDto("testuser1@naver.com");

        ResultActions result = mockMvc.perform(post("/v1/deleteProtege")
                .header("Authorization", accessToken)
                .content(objectMapper.writeValueAsString(deleteProtegeRequestDto))
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("errorCode").value("유저서비스 오류 4번"));
    }

    @Test
    public void deleteProtegeFailBecauseUserIsNotGuardian() throws Exception {
        DeleteProtegeRequestDto deleteProtegeRequestDto = new DeleteProtegeRequestDto("testuser1@naver.com");

        ResultActions result = mockMvc.perform(post("/v1/deleteProtege")
                .header("Authorization", signInResponseDto1.getAccessToken())
                .content(objectMapper.writeValueAsString(deleteProtegeRequestDto))
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("errorCode").value("피보호자 추가 오류 1번"));
    }
}
