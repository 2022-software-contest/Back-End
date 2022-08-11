package com.soongsil.swcontest.pillControllerTest;

import com.soongsil.swcontest.common.BaseTest;
import com.soongsil.swcontest.dto.request.RegisterPillTimeRequestDto;
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
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class DeletePillTimeTest extends BaseTest {
    private SignInResponseDto signInResponseDto1;
    private SignInResponseDto signInResponseDto2;
    private SignInResponseDto signInResponseDto3;

    private List<RegisterPillTimeRequestDto> registerPillTimeRequestDtos = new ArrayList<>();

    private List<RegisterPillTimeRequestDto.SpecificTime> specificTimes = new ArrayList<>();

    private MockedStatic mocked;

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
        pushTokenRepository.save(new PushToken(null, "token", userInfo2));
        pushTokenRepository.save(new PushToken(null, "token", userInfo3));


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

        mocked = mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS);
        LocalDateTime now = LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(), LocalDateTime.now().plusDays(1).getDayOfMonth(),
                0, 0, 0);
        mocked.when(LocalDateTime::now).thenReturn(now);
    }

    @AfterEach
    public void setDown() throws SchedulerException {
        mocked.close();
        for (String groupName : scheduler.getJobGroupNames()) {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                String jobName = jobKey.getName();
                jobService.deleteJob(scheduler, jobName);
            }
        }
    }

    @Test
    @DisplayName("약 시간 삭제테스트(성공)")
    public void deletePillTimeSuccess() throws Exception {
        List<Long> pillIdList = pillService.registerPillTime("testuser1@naver.com", registerPillTimeRequestDtos);

        pillIdList.remove(2);
        pillIdList.add(1000L);
        ResultActions result = mockMvc.perform(post("/v1/deletePill")
                .header("Authorization", signInResponseDto1.getAccessToken())
                .content(objectMapper.writeValueAsString(pillIdList))
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        List<Pill> pills = pillRepository.findAll();
        assertEquals(pills.size(),1);

        List<String> jobkeys = new ArrayList<>();
        for (String groupName : scheduler.getJobGroupNames()) {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                jobkeys.add(jobKey.getName());
            }
        }

        assertEquals(jobkeys.size(),1);
    }

    @Test
    @DisplayName("약 시간 삭제테스트(실패) - 없는 유저일 때")
    public void deletePillTimeFailUserNotFound() throws Exception {
        TokenInfo tokenInfo = jwtTokenProvider.createJwtAccessToken("test@naver.com");

        ResultActions result = mockMvc.perform(post("/v1/deletePill")
                .header("Authorization", tokenInfo.getToken())
                .content(objectMapper.writeValueAsString(List.of(1,2)))
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("유저서비스 오류 4번"));
    }

    @Test
    @DisplayName("약 시간 삭제테스트(실패) - 사용자가 보호자 일 때")
    public void deletePillTimeFailUserIsGuardian() throws Exception {
        ResultActions result = mockMvc.perform(post("/v1/deletePill")
                .header("Authorization", signInResponseDto2.getAccessToken())
                .content(objectMapper.writeValueAsString(List.of(1,2)))
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("보호자-피보호자 오류 2번"));
    }
}
