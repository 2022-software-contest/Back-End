package com.soongsil.swcontest.userControllerTest;

import com.soongsil.swcontest.common.BaseTest;
import com.soongsil.swcontest.dto.request.ChangePasswordRequestDto;
import com.soongsil.swcontest.dto.response.SignInResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ChangePasswordTest extends BaseTest {
    private SignInResponseDto signInResponseDto;
    private ChangePasswordRequestDto changePasswordRequestDto;

    @BeforeEach
    public void setup() {
        makeUser("testuser1@naver.com");
        signInResponseDto = signInUser("testuser1@naver.com");
        changePasswordRequestDto = new ChangePasswordRequestDto(password, "newPassword");
    }

    @Test
    @DisplayName("비밀번호 변경 테스트(성공)")
    public void changePasswordTestSuccess() throws Exception {
        // given
        // when
        ResultActions result = mockMvc.perform(post("/v1/changePassword")
                .header("Authorization",signInResponseDto.getRefreshToken())
                .content(objectMapper.writeValueAsString(changePasswordRequestDto))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk());
    }

    @Test
    @DisplayName("비밀번호 변경 테스트(실패)-없는 유저의 토큰일 때")
    public void changePasswordBecauseInvalidUser() throws Exception {
        // given
        String accessToken = jwtTokenProvider.createJwtAccessToken("email@email.com").getToken();
        // when
        ResultActions result = mockMvc.perform(post("/v1/changePassword")
                .header("Authorization",accessToken)
                .content(objectMapper.writeValueAsString(changePasswordRequestDto))
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("유저서비스 오류 4번"));
    }

    @Test
    @DisplayName("비밀번호 변경 테스트(실패)-oldPassword를 틀렸을 때")
    public void changePasswordFailBecausePasswordIncorrect() throws Exception {
        // given
        changePasswordRequestDto = new ChangePasswordRequestDto("wrongPassword", "newPassword");
        // when
        ResultActions result = mockMvc.perform(post("/v1/changePassword")
                .header("Authorization",signInResponseDto.getAccessToken())
                .content(objectMapper.writeValueAsString(changePasswordRequestDto))
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("유저서비스 오류 2번"));
    }

    @Test
    @DisplayName("비밀번호 변경 테스트(실패)-oldPassword와 newPassword가 같을 때")
    public void changePasswordFailBecauseOldPasswordEqualsNewPassword() throws Exception {
        // given
        changePasswordRequestDto = new ChangePasswordRequestDto(password, password);
        // when
        ResultActions result = mockMvc.perform(post("/v1/changePassword")
                .header("Authorization",signInResponseDto.getAccessToken())
                .content(objectMapper.writeValueAsString(changePasswordRequestDto))
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("유저서비스 오류 7번"));
    }
}
