package com.soongsil.swcontest.service;

import com.soongsil.swcontest.dto.request.RegisterPillTimeRequestDto;
import com.soongsil.swcontest.entity.Pill;
import com.soongsil.swcontest.entity.PushToken;
import com.soongsil.swcontest.entity.UserInfo;
import com.soongsil.swcontest.exception.guardianProtegeServiceException.UserIsGuardianException;
import com.soongsil.swcontest.exception.userServiceException.UserNotFoundException;
import com.soongsil.swcontest.repository.PillRepository;
import com.soongsil.swcontest.repository.PushTokenRepository;
import com.soongsil.swcontest.repository.UserInfoRepository;
import com.soongsil.swcontest.schedule.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PillService {
    private final UserInfoRepository userInfoRepository;
    private final PillRepository pillRepository;
    private final JobService jobService;
    private final Scheduler scheduler;
    private final PushTokenRepository pushTokenRepository;

    public List<Long> registerPillTime(String email, List<RegisterPillTimeRequestDto> registerDoseTimeRequestDtos) {
        UserInfo userInfo = userInfoRepository.findByEmail(email);
        if (userInfo==null) {
            throw new UserNotFoundException("사용자를 찾을 수 없습니다.");
        }

        if (userInfo.getIsGuardian()) {
            throw new UserIsGuardianException("사용자가 보호자 이므로 약먹을 시간을 등록할 수 없습니다.");
        }

        PushToken pushToken = pushTokenRepository.findByUserInfo(userInfo);

        List<Long> registerPillTimeResponseDtos = new ArrayList<>();
        for (RegisterPillTimeRequestDto registerDoseTimeRequestDto : registerDoseTimeRequestDtos) {
            for (RegisterPillTimeRequestDto.SpecificTime specificTime : registerDoseTimeRequestDto.getEatTime()) {
                try {
                    LocalDateTime time = LocalDateTime.of(
                            registerDoseTimeRequestDto.getDateYear(),
                            registerDoseTimeRequestDto.getDateMonth(),
                            registerDoseTimeRequestDto.getDateDay(),
                            specificTime.getHour(),
                            specificTime.getMinutes(),
                            specificTime.getSec());
                    Long id = pillRepository.save(
                            new Pill(
                                    null,
                                    userInfo,
                                    registerDoseTimeRequestDto.getPillName(),
                                    registerDoseTimeRequestDto.getPillCategory(),
                                    time
                            )
                    ).getId();
                    registerPillTimeResponseDtos.add(id);
                    if (pushToken != null) {
                        jobService.registerJob(scheduler, id.toString(), pushToken.getToken(), time);
                    }
                } catch (DateTimeException e) {
                    log.warn("LocalDateTime으로 바꾸는 중 오류가 발생했습니다. 입력받은 값={} {} {} {} {} {}",
                            registerDoseTimeRequestDto.getDateYear(),
                            registerDoseTimeRequestDto.getDateMonth(),
                            registerDoseTimeRequestDto.getDateDay(),
                            specificTime.getHour(),
                            specificTime.getMinutes(),
                            specificTime.getSec());
                }
            }
        }
        return registerPillTimeResponseDtos;
    }

    public void deletePillTime(String email, List<Long> deletePillIdList) {
        UserInfo userInfo = userInfoRepository.findByEmail(email);
        if (userInfo==null) {
            throw new UserNotFoundException("사용자를 찾을 수 없습니다.");
        }

        for (Long id : deletePillIdList) {
            pillRepository.deleteById(id);
        }
    }
}
