package com.soongsil.swcontest.pillControllerTest;

import com.soongsil.swcontest.common.BaseTest;
import com.soongsil.swcontest.dto.request.RegisterPillTimeRequestDto;
import com.soongsil.swcontest.dto.response.SignInResponseDto;
import com.soongsil.swcontest.entity.GuardianProtege;
import com.soongsil.swcontest.entity.Pill;
import com.soongsil.swcontest.entity.UserInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GetPillTimeOnlyProtegeTest extends BaseTest {
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

        pillService.registerPillTime("testuser1@naver.com", registerPillTimeRequestDtos);
    }

//    @Test
//    @DisplayName("환자가 본인 약 시간 얻기테스트(성공)")
//    public void getPillTimeOnlyProtegeSuccess() throws Exception {
//        ResultActions result = mockMvc.perform(get("/v1/getPillProtege")
//                .header("Authorization", signInResponseDto1.getAccessToken())
//                .contentType(MediaType.APPLICATION_JSON));
//
//        result.andExpect(status().isOk())
//                .andExpect(jsonPath("[:0].pillName").value("약1"));
////        Assertions.assertEquals(jsonPath("$.[0].pillName").value("약1"),"약1");
//        List<Pill> pills = pillRepository.findAll();
//        Assertions.assertEquals(pills.size(),3);
//    }
}
