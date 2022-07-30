package com.soongsil.swcontest.service;

import com.soongsil.swcontest.dto.response.AddProtegeResponseDto;
import com.soongsil.swcontest.entity.GuardianProtege;
import com.soongsil.swcontest.entity.UserInfo;
import com.soongsil.swcontest.exception.guardianServiceException.ProtegeIsDuplicateException;
import com.soongsil.swcontest.exception.guardianServiceException.ProtegeIsGuardianException;
import com.soongsil.swcontest.exception.guardianServiceException.ProtegePhoneNumberNotEqualException;
import com.soongsil.swcontest.exception.guardianServiceException.UserIsNotGuardianException;
import com.soongsil.swcontest.exception.userServiceException.UserNotFoundException;
import com.soongsil.swcontest.repository.GuardianProtegeRepository;
import com.soongsil.swcontest.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GuardianProtegeService {
    private final UserInfoRepository userInfoRepository;
    private final GuardianProtegeRepository guardianProtegeRepository;

    @Transactional
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

    @Transactional
    public void deleteProtege(String guardianEmail, String protegeEmail) {
        UserInfo guardianInfo = userInfoRepository.findByEmail(guardianEmail);

        if(guardianInfo==null) {
            throw new UserNotFoundException("사용자(보호자)를 찾을 수 없습니다.");
        }

        if(!guardianInfo.getIsGuardian()) {
            throw new UserIsNotGuardianException("사용자가 보호자가 아닙니다.");
        }

        List<GuardianProtege> guardianProteges = guardianProtegeRepository.findAllByGuardian(guardianInfo);
        for (GuardianProtege guardianProtege : guardianProteges) {
            if(guardianProtege.getProtege().getEmail().equals(protegeEmail)) {
                guardianProtegeRepository.delete(guardianProtege);
                break;
            }
        }
    }
}
