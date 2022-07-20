package com.soongsil.swcontest.imageControllerTest;

import com.soongsil.swcontest.common.BaseTest;
import com.soongsil.swcontest.dto.request.DeleteImageRequestDto;
import com.soongsil.swcontest.dto.response.SignInResponseDto;
import com.soongsil.swcontest.entity.Image;
import com.soongsil.swcontest.entity.UserInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DeleteImageTest extends BaseTest {
    private SignInResponseDto signInResponseDto;

    private final String testImageUrl1 = "https://taewoon-s3.s3.ap-northeast-2.amazonaws.com/image/aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa.PNG";
    private final String testImageUrl2 = "https://taewoon-s3.s3.ap-northeast-2.amazonaws.com/image/bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb.PNG";

    @BeforeEach
    public void setup() {
        makeUser();
        signInResponseDto = signInUser();
        UserInfo testUser = userInfoRepository.findByEmail(signInResponseDto.getEmail());
        imageRepository.save(new Image(null, testImageUrl1, testUser));
        imageRepository.save(new Image(null, testImageUrl2, testUser));
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("이미지 삭제 테스트(성공)")
    public void deleteImageTestSuccess() throws Exception {
        //given
        UserInfo testUser = userInfoRepository.findByEmail(signInResponseDto.getEmail());
        System.out.println(testUser.getImages());
        DeleteImageRequestDto deleteImageRequestDto = new DeleteImageRequestDto(new ArrayList<>() {{
            add(testImageUrl1);
            add(testImageUrl2);
        }});
        //when
        ResultActions result = mockMvc.perform(post("/v1/deleteImage")
                .header("Authorization",signInResponseDto.getAccessToken())
                .content(objectMapper.writeValueAsString(deleteImageRequestDto))
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("이미지 삭제 테스트(실패) - 잘못된 링크일때(Validationn에 걸리는 경우)")
    public void deleteImageFailBecauseInvalidLink() throws Exception {
        //given
        DeleteImageRequestDto deleteImageRequestDto = new DeleteImageRequestDto(new ArrayList<>() {{
            add("aaaa");
            add("bbbb");
        }});

        //when
        ResultActions result = mockMvc.perform(post("/v1/deleteImage")
                .header("Authorization",signInResponseDto.getAccessToken())
                .content(objectMapper.writeValueAsString(deleteImageRequestDto))
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("시스템 오류 1번"));
    }

    @Test
    @DisplayName("이미지 삭제 테스트(실패) - 없는 유저의 토큰일 때")
    public void deleteImageFailBecauseInvalidUser() throws Exception {
        //given
        String accessToken = jwtTokenProvider.createJwtAccessToken("email@email.com").getToken();
        DeleteImageRequestDto deleteImageRequestDto = new DeleteImageRequestDto(new ArrayList<>() {{
            add(testImageUrl1);
            add(testImageUrl2);
        }});

        //when
        ResultActions result = mockMvc.perform(post("/v1/deleteImage")
                .header("Authorization",accessToken)
                .content(objectMapper.writeValueAsString(deleteImageRequestDto))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("유저서비스 오류 4번"));
    }

    @Test
    @DisplayName("이미지 삭제 테스트(실패) - 유저가 가지고 있는 이미지가 아닐 때")
    public void deleteImageFailBecauseUserDoesNotHaveImage() throws Exception {
        //given
        DeleteImageRequestDto deleteImageRequestDto = new DeleteImageRequestDto(new ArrayList<>() {{
            add("https://taewoon-s3.s3.ap-northeast-2.amazonaws.com/image/cccccccccccccccccccccccccccccccccccc.PNG");
        }});

        //when
        ResultActions result = mockMvc.perform(post("/v1/deleteImage")
                .header("Authorization",signInResponseDto.getAccessToken())
                .content(objectMapper.writeValueAsString(deleteImageRequestDto))
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("이미지서비스 오류 2번"));
    }
}
