package com.soongsil.swcontest.guardianProtegeControllerTest;

import com.soongsil.swcontest.common.BaseTest;
import com.soongsil.swcontest.dto.request.DeleteProtegeRequestDto;
import com.soongsil.swcontest.dto.response.SignInResponseDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("보호자가 등록한 피보호자 삭제(성공)")
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
    @DisplayName("보호자가 등록한 피보호자 삭제 (실패) - 보호자가 없을 때")
    public void deleteProtegeFailBecauseGuardianNotFound() throws Exception {
        String accessToken = jwtTokenProvider.createJwtAccessToken("email@email.com").getToken();
        DeleteProtegeRequestDto deleteProtegeRequestDto = new DeleteProtegeRequestDto("testuser1@naver.com");

        ResultActions result = mockMvc.perform(post("/v1/deleteProtege")
                .header("Authorization", accessToken)
                .content(objectMapper.writeValueAsString(deleteProtegeRequestDto))
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("유저서비스 오류 4번"));
    }

    @Test
    @DisplayName("보호자가 등록한 피보호자 삭제 (실패) - 요청보낸 사람이 보호자가 아닐 때")
    public void deleteProtegeFailBecauseUserIsNotGuardian() throws Exception {
        DeleteProtegeRequestDto deleteProtegeRequestDto = new DeleteProtegeRequestDto("testuser1@naver.com");

        ResultActions result = mockMvc.perform(post("/v1/deleteProtege")
                .header("Authorization", signInResponseDto1.getAccessToken())
                .content(objectMapper.writeValueAsString(deleteProtegeRequestDto))
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("보호자-피보호자 오류 1번"));
    }

    @Test
    @DisplayName("보호자가 등록한 피보호자 삭제 (실패) - 삭제할 사람이 유저목록에 없을 때")
    public void deleteProtegeFailBecauseProtegeIsNotFound() throws Exception {
        DeleteProtegeRequestDto deleteProtegeRequestDto = new DeleteProtegeRequestDto("test@naver.com");

        ResultActions result = mockMvc.perform(post("/v1/deleteProtege")
                .header("Authorization", signInResponseDto2.getAccessToken())
                .content(objectMapper.writeValueAsString(deleteProtegeRequestDto))
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("유저서비스 오류 4번"));
    }
}
