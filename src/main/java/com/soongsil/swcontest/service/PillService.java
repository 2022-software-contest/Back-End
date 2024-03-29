package com.soongsil.swcontest.service;

import com.soongsil.swcontest.dto.request.RegisterPillTimeRequestDto;
import com.soongsil.swcontest.entity.GuardianProtege;
import com.soongsil.swcontest.entity.Pill;
import com.soongsil.swcontest.entity.PushToken;
import com.soongsil.swcontest.entity.UserInfo;
import com.soongsil.swcontest.exception.guardianProtegeServiceException.GuardianHasNotProtegeException;
import com.soongsil.swcontest.exception.guardianProtegeServiceException.UserIsGuardianException;
import com.soongsil.swcontest.exception.pillServiceException.MinuteNotDivideFiveException;
import com.soongsil.swcontest.exception.userServiceException.UserNotFoundException;
import com.soongsil.swcontest.repository.GuardianProtegeRepository;
import com.soongsil.swcontest.repository.PillRepository;
import com.soongsil.swcontest.repository.PushTokenRepository;
import com.soongsil.swcontest.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PillService {
    private final UserInfoRepository userInfoRepository;

    private final GuardianProtegeRepository guardianProtegeRepository;

    private final PillRepository pillRepository;
    private final PushTokenRepository pushTokenRepository;
    private final FirebaseCloudMessageService firebaseCloudMessageService;

    public List<Long> registerPillTime(String email, List<RegisterPillTimeRequestDto> registerDoseTimeRequestDtos) {
        UserInfo userInfo = userInfoRepository.findByEmail(email);
        if (userInfo==null) {
            throw new UserNotFoundException("사용자를 찾을 수 없습니다.");
        }

        if (userInfo.getIsGuardian()) {
            throw new UserIsGuardianException("사용자가 보호자 이므로 약먹을 시간을 등록할 수 없습니다.");
        }

        List<Long> registerPillTimeResponseDtos = new ArrayList<>();
        for (RegisterPillTimeRequestDto registerDoseTimeRequestDto : registerDoseTimeRequestDtos) {
            for (RegisterPillTimeRequestDto.SpecificTime specificTime : registerDoseTimeRequestDto.getEatTime()) {
                try {
                    if ((specificTime.getMinutes()%5)!=0) {
                        throw new MinuteNotDivideFiveException("입력한 분은 5분 단위로 나눌수 있어야합니다.");
                    }

                    LocalDateTime time = LocalDateTime.of(
                            registerDoseTimeRequestDto.getDateYear(),
                            registerDoseTimeRequestDto.getDateMonth(),
                            registerDoseTimeRequestDto.getDateDay(),
                            specificTime.getHour(),
                            specificTime.getMinutes(),
                            specificTime.getSec());

                    if (time.isBefore(LocalDateTime.now())) {
                        continue;
                    }

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
                } catch (DateTimeException e) {
                    log.warn("LocalDateTime으로 바꾸는 중 오류가 발생했습니다. 입력받은 값={} {} {} {} {} {}",
                            registerDoseTimeRequestDto.getDateYear(),
                            registerDoseTimeRequestDto.getDateMonth(),
                            registerDoseTimeRequestDto.getDateDay(),
                            specificTime.getHour(),
                            specificTime.getMinutes(),
                            specificTime.getSec());
                } catch (MinuteNotDivideFiveException e) {
                    log.warn("입력받은 분이 5로 나눈 몫이 0이 아님. 입력받은 값={} {} {} {} {} {}",
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

    public void deletePillTime(String protegeEmail, List<Long> deletePillIdList) {
        UserInfo protegeInfo = userInfoRepository.findByEmail(protegeEmail);
        if (protegeInfo==null) {
            throw new UserNotFoundException("사용자를 찾을 수 없습니다.");
        }

        if (protegeInfo.getIsGuardian()) {
            throw new UserIsGuardianException("사용자가 보호자 입니다.");
        }

        List<GuardianProtege> guardians = guardianProtegeRepository.findAllByProtege(protegeInfo);
        for (GuardianProtege guardian : guardians) {
            PushToken pushToken = pushTokenRepository.findByUserInfo(guardian.getGuardian());
            if (pushToken != null) {
                for (Long id : deletePillIdList) {
                    Optional<Pill> pillRepositoryById = pillRepository.findById(id);
                    pillRepositoryById.ifPresent(
                            pill -> firebaseCloudMessageService.sendMessageTo(
                                    pushToken.getToken(),
                                    "환자 약 삭제 알림",
                                    protegeEmail + " 환자가 마감일이 " + pill.getTime() + "인 " + pill.getPillName() +"정을 삭제했습니다.", "delete"));
                }
            }
        }

        for (Long id : deletePillIdList) {
            if (pillRepository.findById(id).isPresent()) {
                pillRepository.deleteById(id);
            }
        }
    }

    public List<Pill> getPillTimeOnlyProtege(String protegeEmail) {
        UserInfo protegeInfo = userInfoRepository.findByEmail(protegeEmail);
        if (protegeInfo==null) {
            throw new UserNotFoundException("사용자를 찾을 수 없습니다.");
        }

        if (protegeInfo.getIsGuardian()) {
            throw new UserIsGuardianException("사용자가 보호자 입니다. 보호자 전용 API를 사용해주세요!");
        }

        return pillRepository.findByUserInfo(protegeInfo);
    }

    public List<Pill> getPillTimeOnlyGuardian(String guardianEmail, String protegeEmail) {
        UserInfo guardianInfo = userInfoRepository.findByEmail(guardianEmail);
        if (guardianInfo==null) {
            throw new UserNotFoundException("보호자를 찾을 수 없습니다.");
        }

        UserInfo protegeInfo = userInfoRepository.findByEmail(protegeEmail);
        if (protegeInfo==null) {
            throw new UserNotFoundException("환자를 찾을 수 없습니다.");
        }

        if (guardianInfo.getIsGuardian()) {
            if(!guardianProtegeRepository.existsByGuardianAndProtege(guardianInfo, protegeInfo)) {
                throw new GuardianHasNotProtegeException("보호자가 해당 환자를 등록하지 않았습니다.");
            }
            return pillRepository.findByUserInfo(protegeInfo);
        }
        else {
            throw new UserIsGuardianException("사용자가 환자 입니다. 환자 전용 API를 사용해주세요!");
        }
    }

    public void reportPillTime(String protegeEmail, String pillNames) {
        UserInfo protegeInfo = userInfoRepository.findByEmail(protegeEmail);
        if(protegeInfo==null) {
            throw new UserNotFoundException("환자본인을 찾을 수 없습니다.");
        }

        List<GuardianProtege> guardians = guardianProtegeRepository.findAllByProtege(protegeInfo);
        for (GuardianProtege guardian : guardians) {
            UserInfo guardianInfo = userInfoRepository.findByEmail(guardian.getGuardian().getEmail());
            if (guardianInfo!=null) {
                PushToken pushToken = pushTokenRepository.findByUserInfo(guardianInfo);
                if(pushToken!=null && pushToken.getToken()!=null) {
                    firebaseCloudMessageService.sendMessageTo(
                            pushToken.getToken(),
                            "환자가 약을 먹지 않았습니다.",
                            protegeEmail+"환자가 " + pillNames + "(정)을 먹지 않았습니다.",
                            "not eat");
                }

            }
        }
    }
}
