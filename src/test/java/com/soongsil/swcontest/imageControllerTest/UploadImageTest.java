package com.soongsil.swcontest.imageControllerTest;

import com.soongsil.swcontest.common.BaseTest;
import com.soongsil.swcontest.dto.response.SignInResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UploadImageTest extends BaseTest {

    private SignInResponseDto signInResponseDto;
    private List<MockMultipartFile> mockMultipartFiles;

    @BeforeEach
    public void setup() {
        makeUser("testuser1@naver.com");
        signInResponseDto = signInUser("testuser1@naver.com");
        mockMultipartFiles = makeTwoImages();
    }

    @Test
    @DisplayName("이미지 업로드 테스트(성공)")
    public void uploadImageTestSuccess() throws Exception {
        //given
        //when
        ResultActions result = mockMvc.perform(
                multipart("/v1/uploadImage")
                        .file(mockMultipartFiles.get(0))
                        .file(mockMultipartFiles.get(1))
                        .header("Authorization", signInResponseDto.getAccessToken()));
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.imageUrls.size()").value(2));
    }

    @Test
    @DisplayName("이미지 업로드 테스트(실패) - 없는 유저의 토큰일 때")
    public void uploadImageFailBecauseInvalidUser() throws Exception {
        // given
        String accessToken = jwtTokenProvider.createJwtAccessToken("email@email.com").getToken();
        // when
        ResultActions result = mockMvc.perform(
                multipart("/v1/uploadImage")
                        .file(mockMultipartFiles.get(0))
                        .file(mockMultipartFiles.get(1))
                        .header("Authorization", accessToken));
        //then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("유저서비스 오류 4번"));
    }
}
