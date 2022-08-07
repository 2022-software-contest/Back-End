package com.soongsil.swcontest.userControllerTest;

import com.soongsil.swcontest.common.BaseTest;
import com.soongsil.swcontest.dto.request.SignInRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class SignInTest extends BaseTest {
    @BeforeEach
    private void setUp() {
        userService.signUp(email, password, username, phoneNumber, isGuardian, role);
    }

    @Test
    @DisplayName("로그인 테스트(성공)")
    public void signInTestSuccess() throws Exception {
        // given
        SignInRequestDto signInRequestDto = new SignInRequestDto(email, password);
        // when
        ResultActions result = mockMvc.perform(post("/v1/signIn")
                .content(objectMapper.writeValueAsString(signInRequestDto))
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("email").value(email))
                .andExpect(jsonPath("username").value(username))
                .andExpect(jsonPath("role").value(role.toString()))
                .andExpect(jsonPath("accessToken").exists())
                .andExpect(jsonPath("refreshToken").exists());
    }

    @Test
    @DisplayName("로그인 테스트(실패)-Email 형식이 아닐때")
    public void signInFailBecauseInvalidEmailFormat() throws Exception {
        // given
        SignInRequestDto signInRequestDto = new SignInRequestDto("email", password);
        // when
        ResultActions result = mockMvc.perform(post("/v1/signIn")
                .content(objectMapper.writeValueAsString(signInRequestDto))
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("시스템 오류 1번"));
    }

    @Test
    @DisplayName("로그인 테스트(실패)-Email이 공백일 때")
    public void signInFailBecauseEmailIsBlank() throws Exception {
        // given
        SignInRequestDto signInRequestDto = new SignInRequestDto(" ", password);
        // when
        ResultActions result = mockMvc.perform(post("/v1/signIn")
                .content(objectMapper.writeValueAsString(signInRequestDto))
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("시스템 오류 1번"));
    }

    @Test
    @DisplayName("로그인 테스트(실패)-Email이 없을때")
    public void signInFailBecauseEmailNotFound() throws Exception {
        // given
        SignInRequestDto signInRequestDto = new SignInRequestDto("test1@naver.com", password);
        // when
        ResultActions result = mockMvc.perform(post("/v1/signIn")
                .content(objectMapper.writeValueAsString(signInRequestDto))
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("유저서비스 오류 4번"));
    }

    @Test
    @DisplayName("로그인 테스트(실패)-Password가 공백일 때")
    public void signInFailBecausePasswordIsBlank() throws Exception {
        // given
        SignInRequestDto signInRequestDto = new SignInRequestDto(email, " ");
        // when
        ResultActions result = mockMvc.perform(post("/v1/signIn")
                .content(objectMapper.writeValueAsString(signInRequestDto))
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("시스템 오류 1번"));
    }

    @Test
    @DisplayName("로그인 테스트(실패)-Password가 틀릴때")
    public void signInFailBecauseWrongPassword() throws Exception {
        // given
        SignInRequestDto signInRequestDto = new SignInRequestDto(email, "password1");
        // when
        ResultActions result = mockMvc.perform(post("/v1/signIn")
                .content(objectMapper.writeValueAsString(signInRequestDto))
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("유저서비스 오류 2번"));
    }

    @Test
    @DisplayName("로그인 테스트(실패) - 잘못된 Media Type 요청")
    public void signInFailBecauseInvalidMediaTypeRequest() throws Exception {
        // given
        SignInRequestDto signInRequestDto = new SignInRequestDto(" ", password);
        // when
        ResultActions result = mockMvc.perform(post("/v1/signIn")
                .content(objectMapper.writeValueAsString(signInRequestDto))
                .contentType(MediaType.APPLICATION_ATOM_XML));
        //then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("시스템 오류 2번"));
    }
}
