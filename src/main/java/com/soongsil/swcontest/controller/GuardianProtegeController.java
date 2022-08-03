package com.soongsil.swcontest.controller;

import com.soongsil.swcontest.dto.request.AddProtegeRequestDto;
import com.soongsil.swcontest.dto.request.DeleteProtegeRequestDto;
import com.soongsil.swcontest.dto.request.GetProtegePillRecordsRequestDto;
import com.soongsil.swcontest.dto.response.AddProtegeResponseDto;
import com.soongsil.swcontest.dto.response.GetGuardiansResponseDto;
import com.soongsil.swcontest.dto.response.GetProtegePillRecordsResponseDto;
import com.soongsil.swcontest.dto.response.GetProtegesResponseDto;
import com.soongsil.swcontest.security.AuthInfo;
import com.soongsil.swcontest.security.Authenticated;
import com.soongsil.swcontest.service.GuardianProtegeService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class GuardianProtegeController {

    private final GuardianProtegeService guardianProtegeService;

    @ApiOperation("피보호자 1명 추가(보호자인 경우만 사용 가능)")
    @PostMapping("/v1/addProtege")
    public AddProtegeResponseDto addProtege(@Authenticated AuthInfo authInfo, @Valid @RequestBody AddProtegeRequestDto addProtegeRequestDto) {
        return guardianProtegeService.addProtege(authInfo.getEmail(),
                addProtegeRequestDto.getProtegeEmail(), addProtegeRequestDto.getPhoneNumber());
    }

    @ApiOperation("피보호자 삭제(보호자인 경우만 사용 가능)")
    @PostMapping("/v1/deleteProtege")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProtege(@Authenticated AuthInfo authInfo, @Valid @RequestBody DeleteProtegeRequestDto deleteProtegeRequestDto) {
        guardianProtegeService.deleteProtege(authInfo.getEmail(),
                deleteProtegeRequestDto.getProtegeEmail());
    }

    @ApiOperation("보호자가 관리하는 피보호자의 복용기록 가져오기(보호자만 사용)")
    @PostMapping("/v1/getProtegePillRecord")
    public GetProtegePillRecordsResponseDto getProtegePillRecords(@Authenticated AuthInfo authInfo, @Valid @RequestBody GetProtegePillRecordsRequestDto getProtegePillRecordsRequest) {
        return guardianProtegeService.getProtegePillRecords(authInfo.getEmail(), getProtegePillRecordsRequest.getEmail());
    }

    @ApiOperation("환자를 관리하는 보호자 리스트 가져오기(환자만 사용)")
    @GetMapping("/v1/getGuardian")
    public GetGuardiansResponseDto getGuardians(@Authenticated AuthInfo authInfo) {
        return guardianProtegeService.getGuardians(authInfo.getEmail());
    }

    @ApiOperation("보호자가 관리하는 환자 리스트 가져오기(보호자만 사용)")
    @GetMapping("/v1/getProtege")
    public GetProtegesResponseDto getProteges(@Authenticated AuthInfo authInfo) {
        return guardianProtegeService.getProteges(authInfo.getEmail());
    }
}
