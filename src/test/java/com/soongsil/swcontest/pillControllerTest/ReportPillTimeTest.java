package com.soongsil.swcontest.pillControllerTest;

import com.soongsil.swcontest.common.BaseTest;
import com.soongsil.swcontest.dto.request.ReportPillTimeRequestDto;
import com.soongsil.swcontest.dto.response.SignInResponseDto;
import com.soongsil.swcontest.entity.GuardianProtege;
import com.soongsil.swcontest.entity.Pill;
import com.soongsil.swcontest.entity.PushToken;
import com.soongsil.swcontest.entity.UserInfo;
import com.soongsil.swcontest.jwt.TokenInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.quartz.SchedulerException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReportPillTimeTest extends BaseTest {
    private SignInResponseDto signInResponseDto1;
    private SignInResponseDto signInResponseDto2;
    private SignInResponseDto signInResponseDto3;

    private ReportPillTimeRequestDto reportPillTimeRequestDto;

    private MockedStatic mocked;
    @BeforeEach
    public void setUp() {
        makeUser("testuser1@naver.com", false);
        signInResponseDto1 = signInUser("testuser1@naver.com");
        makeUser("testuser2@naver.com", true);
        signInResponseDto2 = signInUser("testuser2@naver.com");
        makeUser("testuser3@naver.com", true);
        signInResponseDto3 = signInUser("testuser3@naver.com");
        UserInfo userInfo1 = userInfoRepository.findByEmail("testuser1@naver.com");
        UserInfo userInfo2 = userInfoRepository.findByEmail("testuser2@naver.com");
        UserInfo userInfo3 = userInfoRepository.findByEmail("testuser3@naver.com");
        guardianProtegeRepository.save(
                new GuardianProtege(
                        null, userInfo2, userInfo1));
        guardianProtegeRepository.save(
                new GuardianProtege(
                        null, userInfo3, userInfo1));

        pushTokenRepository.save(new PushToken(null, "token1", userInfo1));
        pushTokenRepository.save(new PushToken(null, "token2", userInfo2));
        pushTokenRepository.save(new PushToken(null, "token3", userInfo3));
        mocked = mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS);
        LocalDateTime now1 = LocalDateTime.of(
                LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(), LocalDateTime.now().plusDays(1).getDayOfMonth(),
                12, 0, 0);

        pillRepository.save(new Pill(null, userInfo1, "약1", "카테고리1", now1));
        LocalDateTime now2 = LocalDateTime.of(
                LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(), LocalDateTime.now().plusDays(1).getDayOfMonth(),
                18, 0, 0);
        mocked.when(LocalDateTime::now).thenReturn(now2);
        reportPillTimeRequestDto = new ReportPillTimeRequestDto("약1");

    }

    @AfterEach
    public void setDown() throws SchedulerException {
        mocked.close();
    }

    @Test
    @DisplayName("약 안먹음 신고 테스트 (성공)")
    public void reportPillTimeSuccess() throws Exception {
        ResultActions result = mockMvc.perform(post("/v1/report")
                .header("Authorization", signInResponseDto1.getAccessToken())
                .content(objectMapper.writeValueAsString(reportPillTimeRequestDto))
                .contentType(MediaType.APPLICATION_JSON));

        then(firebaseCloudMessageService).should(times(1))
                .sendMessageTo("token2", "환자가 약을 먹지 않았습니다.",
                        "testuser1@naver.com환자가 약1(정)을 먹지 않았습니다.",
                        "not eat");

        then(firebaseCloudMessageService).should(times(1))
                .sendMessageTo("token3", "환자가 약을 먹지 않았습니다.",
                        "testuser1@naver.com환자가 약1(정)을 먹지 않았습니다.",
                        "not eat");

        result.andExpect(status().isOk());
    }

    @Test
    @DisplayName("약 안먹음 신고 테스트 (실패) - 환자 본인을 찾을수 없는 경우")
    public void reportPillTimeFailBecauseProtegeNotFound() throws Exception {
        TokenInfo tokenInfo = jwtTokenProvider.createJwtAccessToken("test@naver.com");
        ResultActions result = mockMvc.perform(post("/v1/report")
                .header("Authorization", tokenInfo.getToken())
                .content(objectMapper.writeValueAsString(reportPillTimeRequestDto))
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("유저서비스 오류 4번"));
    }
}
