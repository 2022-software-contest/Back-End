package com.soongsil.swcontest.service;

import com.soongsil.swcontest.dto.response.AddProtegeResponseDto;
import com.soongsil.swcontest.dto.response.GetGuardiansResponseDto;
import com.soongsil.swcontest.dto.response.GetProtegePillRecordsResponseDto;
import com.soongsil.swcontest.dto.response.GetProtegesResponseDto;
import com.soongsil.swcontest.entity.GuardianProtege;
import com.soongsil.swcontest.entity.Pill;
import com.soongsil.swcontest.entity.UserInfo;
import com.soongsil.swcontest.exception.guardianProtegeServiceException.*;
import com.soongsil.swcontest.exception.guardianProtegeServiceException.UserIsGuardianException;
import com.soongsil.swcontest.exception.userServiceException.UserNotFoundException;
import com.soongsil.swcontest.repository.GuardianProtegeRepository;
import com.soongsil.swcontest.repository.PillRepository;
import com.soongsil.swcontest.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GuardianProtegeService {
    private final UserInfoRepository userInfoRepository;
    private final GuardianProtegeRepository guardianProtegeRepository;

    private final PillRepository pillRepository;

    public boolean classifyGuardianProtege(String email) {
        UserInfo userInfo = userInfoRepository.findByEmail(email);
        return userInfo.getIsGuardian();
    }

    public AddProtegeResponseDto addProtege(String guardianEmail, String protegeEmail, String protegePhoneNumber) {
        UserInfo guardianInfo = userInfoRepository.findByEmail(guardianEmail);

        if(guardianInfo==null) {
            throw new UserNotFoundException("사용자(보호자)를 찾을 수 없습니다.");
        }

        if(!guardianInfo.getIsGuardian()) {
            throw new UserIsNotGuardianException("사용자가 보호자가 아닙니다.");
        }

        UserInfo protegeInfo = userInfoRepository.findByEmail(protegeEmail);

        if (protegeInfo==null) {
            throw new UserNotFoundException("피보호자를 찾을 수 없습니다.");
        }

        if (!protegeInfo.getPhoneNumber().equals(protegePhoneNumber)) {
            throw new ProtegePhoneNumberNotEqualException("피보호자의 전화번호가 일치하지 않습니다.");
        }

        if (protegeInfo.getIsGuardian()) {
            throw new ProtegeIsGuardianException("등록 대상이 보호자 입니다.");
        }

        List<GuardianProtege> guardianProteges = guardianProtegeRepository.findAllByGuardian(guardianInfo);
        for (GuardianProtege guardianProtege : guardianProteges) {
            if(guardianProtege.getProtege().getEmail().equals(protegeEmail)) {
                throw new ProtegeIsDuplicateException("피보호자 중복 등록 오류입니다.");
            }
        }
        guardianProtegeRepository.save(new GuardianProtege(null, guardianInfo, protegeInfo));
        return new AddProtegeResponseDto(guardianInfo.getEmail(), protegeInfo.getEmail());
    }

    public void deleteProtege(String guardianEmail, String protegeEmail) {
        UserInfo guardianInfo = userInfoRepository.findByEmail(guardianEmail);

        if(guardianInfo==null) {
            throw new UserNotFoundException("사용자(보호자)를 찾을 수 없습니다.");
        }

        if(!guardianInfo.getIsGuardian()) {
            throw new UserIsNotGuardianException("사용자가 보호자가 아닙니다.");
        }

        UserInfo protegeInfo = userInfoRepository.findByEmail(protegeEmail);
        if(guardianProtegeRepository.existsByGuardianAndProtege(guardianInfo, protegeInfo)) {
            guardianProtegeRepository.deleteByGuardianAndProtege(guardianInfo, protegeInfo);
        } else {
            throw new UserNotFoundException("삭제할 환자가 유저 목록에 없습니다.");
        }
    }

    public GetProtegePillRecordsResponseDto getProtegePillRecords(String guardianEmail, String protegeEmail) {
        UserInfo guardianInfo = userInfoRepository.findByEmail(guardianEmail);

        if(guardianInfo==null) {
            throw new UserNotFoundException("사용자(보호자)를 찾을 수 없습니다.");
        }

        if(!guardianInfo.getIsGuardian()) {
            throw new UserIsNotGuardianException("사용자가 보호자가 아닙니다.");
        }

        UserInfo protegeInfo = userInfoRepository.findByEmail(protegeEmail);
        if (protegeInfo == null) {
            throw new UserNotFoundException("피보호자를 찾을 수 없습니다.");
        }

        if(!guardianProtegeRepository.existsByGuardianAndProtege(guardianInfo, protegeInfo)) {
            throw new GuardianHasNotProtegeException("보호자가 해당 환자를 등록하지 않았습니다.");
        }

        List<Pill> pills = pillRepository.findByUserInfo(protegeInfo);
        return new GetProtegePillRecordsResponseDto(pills.size(), pills);
    }

    public GetGuardiansResponseDto getGuardians(String protegeEmail) {
        UserInfo protegeInfo = userInfoRepository.findByEmail(protegeEmail);
        if (protegeInfo==null) {
            throw new UserNotFoundException("로그인 요청한 피보호자를 찾을 수 없습니다.");
        }

        if (protegeInfo.getIsGuardian()) {
            throw new UserIsGuardianException("환자 전용 API입니다.");
        }

        List<GuardianProtege> guardians = guardianProtegeRepository.findAllByProtege(protegeInfo);
        ArrayList<UserInfo> guardianInfos = new ArrayList<>();
        for (GuardianProtege guardian : guardians) {
            UserInfo userInfo = userInfoRepository.findByEmail(guardian.getGuardian().getEmail());
            guardianInfos.add(userInfo);
        }
        return new GetGuardiansResponseDto(guardianInfos.size(), guardianInfos);
    }

    public GetProtegesResponseDto getProteges(String guardianEmail) {
        UserInfo guardianInfo = userInfoRepository.findByEmail(guardianEmail);
        if (guardianInfo==null) {
            throw new UserNotFoundException("로그인 요청한 보호자를 찾을 수 없습니다.");
        }

        if (!guardianInfo.getIsGuardian()) {
            throw new UserIsNotGuardianException("보호자 전용 API 입니다.");
        }

        List<GuardianProtege> proteges = guardianProtegeRepository.findAllByGuardian(guardianInfo);
        ArrayList<UserInfo> protegeInfos = new ArrayList<>();
        for (GuardianProtege guardian : proteges) {
            UserInfo userInfo = userInfoRepository.findByEmail(guardian.getGuardian().getEmail());
            protegeInfos.add(userInfo);
        }
        return new GetProtegesResponseDto(protegeInfos.size(), protegeInfos);
    }
}
