package com.soongsil.swcontest.pillControllerTest;

import com.soongsil.swcontest.common.BaseTest;
import com.soongsil.swcontest.dto.request.RegisterPillTimeRequestDto;
import com.soongsil.swcontest.dto.response.SignInResponseDto;
import com.soongsil.swcontest.entity.GuardianProtege;
import com.soongsil.swcontest.entity.PushToken;
import com.soongsil.swcontest.entity.UserInfo;
import com.soongsil.swcontest.jwt.TokenInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.quartz.JobKey;
import org.quartz.TriggerKey;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RegisterPillTimeTest extends BaseTest {
    private SignInResponseDto signInResponseDto1;
    private SignInResponseDto signInResponseDto2;
    private SignInResponseDto signInResponseDto3;

    private List<RegisterPillTimeRequestDto> registerPillTimeRequestDtos = new ArrayList<>();

    private List<RegisterPillTimeRequestDto.SpecificTime> specificTimes = new ArrayList<>();
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
        UserInfo userInfo3 = userInfoRepository.findByEmail("testuser3@naver.com");
        guardianProtegeRepository.save(
                new GuardianProtege(
                        null, userInfo2, userInfo1));
        guardianProtegeRepository.save(
                new GuardianProtege(
                        null, userInfo3, userInfo1));

        pushTokenRepository.save(new PushToken(null, "token", userInfo1));

        specificTimes.add(new RegisterPillTimeRequestDto.SpecificTime(
                6, 0, 0
        ));
        specificTimes.add(new RegisterPillTimeRequestDto.SpecificTime(
                12, 0, 0
        ));
        specificTimes.add(new RegisterPillTimeRequestDto.SpecificTime(
                18, 0, 0
        ));

        registerPillTimeRequestDtos.add(new RegisterPillTimeRequestDto(
                "약1",
                "카테고리1",
                specificTimes,
                LocalDateTime.now().getYear(),
                LocalDateTime.now().getMonth().getValue(),
                LocalDateTime.now().plusDays(2).getDayOfMonth()
        ));

        MockedStatic mocked = mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS);
        LocalDateTime now = LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(), LocalDateTime.now().plusDays(1).getDayOfMonth(),
                0, 0, 0);
        mocked.when(LocalDateTime::now).thenReturn(now);
    }

    @Test
    @DisplayName("약먹을 시간 등록 테스트(성공)")
    public void registerPillTimeSuccess() throws Exception {
        ResultActions result = mockMvc.perform(post("/v1/registerPill")
                .header("Authorization", signInResponseDto1.getAccessToken())
                .content(objectMapper.writeValueAsString(registerPillTimeRequestDtos))
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]").value(1))
                .andExpect(jsonPath("$.[1]").value(2))
                .andExpect(jsonPath("$.[2]").value(3));
        assertEquals(scheduler.getTrigger(TriggerKey.triggerKey("1")).getNextFireTime().getTime(),
                LocalDateTime.of(
                        LocalDateTime.now().getYear(),
                        LocalDateTime.now().getMonth().getValue(),
                        LocalDateTime.now().getDayOfMonth(),
                        6,0,0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        assertEquals(scheduler.getTrigger(TriggerKey.triggerKey("2")).getNextFireTime().getTime(),
                LocalDateTime.of(
                        LocalDateTime.now().getYear(),
                        LocalDateTime.now().getMonth().getValue(),
                        LocalDateTime.now().getDayOfMonth(),
                        12,0,0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        assertEquals(scheduler.getTrigger(TriggerKey.triggerKey("3")).getNextFireTime().getTime(),
                LocalDateTime.of(
                        LocalDateTime.now().getYear(),
                        LocalDateTime.now().getMonth().getValue(),
                        LocalDateTime.now().getDayOfMonth(),
                        18,0,0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        assertFalse(scheduler.checkExists(JobKey.jobKey("4")));
    }

//    @Test
//    @DisplayName("약먹을 시간 등록 테스트(실패) - 없는 유저일 때")
//    public void registerPillTimeFailUserNotFound() throws Exception {
//        TokenInfo tokenInfo = jwtTokenProvider.createJwtAccessToken("test@naver.com");
//        ResultActions result = mockMvc.perform(post("/v1/registerPill")
//                .header("Authorization", tokenInfo.getToken())
//                .content(objectMapper.writeValueAsString(registerPillTimeRequestDtos))
//                .contentType(MediaType.APPLICATION_JSON));
//
//        result.andExpect(status().isBadRequest())
//                .andExpect(jsonPath("errorCode").value("유저서비스 오류 4번"));
//    }

//    @Test
//    @DisplayName("약먹을 시간 등록 테스트(실패) - 보호자일 때")
//    public void registerPillTimeFailUserIsGuardian() throws Exception {
//        ResultActions result = mockMvc.perform(post("/v1/registerPill")
//                .header("Authorization", signInResponseDto2.getAccessToken())
//                .content(objectMapper.writeValueAsString(registerPillTimeRequestDtos))
//                .contentType(MediaType.APPLICATION_JSON));
//
//        result.andExpect(status().isBadRequest())
//                .andExpect(jsonPath("errorCode").value("보호자-피보호자 오류 2번"));
//    }
}
