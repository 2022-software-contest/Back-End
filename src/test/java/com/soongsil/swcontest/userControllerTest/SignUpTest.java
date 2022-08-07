package com.soongsil.swcontest.userControllerTest;

import com.soongsil.swcontest.common.BaseTest;
import com.soongsil.swcontest.dto.request.SignUpRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class SignUpTest extends BaseTest {
    @Test
    @DisplayName("회원가입 테스트(성공)")
    public void signUpTestSuccess() throws Exception {
        // given
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto(email, password, username, phoneNumber, isGuardian, role);
        // when
        ResultActions result = mockMvc.perform(post("/v1/signUp")
                .content(objectMapper.writeValueAsString(signUpRequestDto))
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("email").value(email))
                .andExpect(jsonPath("username").value(username))
                .andExpect(jsonPath("role").value(role.toString()));
    }

    @Test
    @DisplayName("회원가입 테스트(실패)-Email 형식이 아닐때")
    public void signUpFailBecauseInvalidEmailFormat() throws Exception {
        // given
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto("email", password, username, phoneNumber, isGuardian, role);
        // when
        ResultActions result = mockMvc.perform(post("/v1/signUp")
                .content(objectMapper.writeValueAsString(signUpRequestDto))
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("시스템 오류 1번"));
    }

    @Test
    @DisplayName("회원가입 테스트(실패)-Email이 공백일 때")
    public void signUpFailBecauseEmailIsBlank() throws Exception {
        // given
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto(" ", password, username, phoneNumber, isGuardian, role);
        // when
        ResultActions result = mockMvc.perform(post("/v1/signUp")
                .content(objectMapper.writeValueAsString(signUpRequestDto))
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("시스템 오류 1번"));
    }

    @Test
    @DisplayName("회원가입 테스트(실패)-Email이 중복됬을때")
    public void signUpFailBecauseDuplicateEmail() throws Exception {
        // given
        makeUser("testuser1@naver.com");
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto(email, password, username, phoneNumber, isGuardian, role);
        // when
        ResultActions result = mockMvc.perform(post("/v1/signUp")
                .content(objectMapper.writeValueAsString(signUpRequestDto))
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("유저서비스 오류 1번"));
    }

    @Test
    @DisplayName("회원가입 테스트(실패)-Password가 공백일 때")
    public void signUpFailBecausePasswordIsBlank() throws Exception {
        // given
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto(email, " ", username, phoneNumber, isGuardian, role);
        // when
        ResultActions result = mockMvc.perform(post("/v1/signUp")
                .content(objectMapper.writeValueAsString(signUpRequestDto))
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("시스템 오류 1번"));
    }

    @Test
    @DisplayName("회원가입 테스트(실패)-Username이 공백일때")
    public void signUpFailBecauseUsernameIsBlank() throws Exception {
        // given
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto(email, password, " ", phoneNumber, isGuardian, role);
        // when
        ResultActions result = mockMvc.perform(post("/v1/signUp")
                .content(objectMapper.writeValueAsString(signUpRequestDto))
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("시스템 오류 1번"));
    }

    @Test
    @DisplayName("회원가입 테스트(실패)-PhoneNumber이 공백일때")
    public void signUpFailBecausePhoneNumberIsBlank() throws Exception {
        // given
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto(email, password, username, " ", isGuardian, role);
        // when
        ResultActions result = mockMvc.perform(post("/v1/signUp")
                .content(objectMapper.writeValueAsString(signUpRequestDto))
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("시스템 오류 1번"));
    }

    @Test
    @DisplayName("회원가입 테스트(실패) - 잘못된 Media Type 요청")
    public void signUpFailBecauseInvalidMediaTypeRequest() throws Exception {
        // given
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto("email", password, username, phoneNumber, isGuardian, role);
        // when
        ResultActions result = mockMvc.perform(post("/v1/signUp")
                .content(objectMapper.writeValueAsString(signUpRequestDto))
                .contentType(MediaType.APPLICATION_ATOM_XML));
        //then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("시스템 오류 2번"));
    }
}
