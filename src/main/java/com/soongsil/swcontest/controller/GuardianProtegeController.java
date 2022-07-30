package com.soongsil.swcontest.controller;

import com.soongsil.swcontest.dto.request.AddProtegeRequestDto;
import com.soongsil.swcontest.dto.request.DeleteProtegeRequestDto;
import com.soongsil.swcontest.dto.response.AddProtegeResponseDto;
import com.soongsil.swcontest.security.AuthInfo;
import com.soongsil.swcontest.security.Authenticated;
import com.soongsil.swcontest.service.GuardianProtegeService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
}
