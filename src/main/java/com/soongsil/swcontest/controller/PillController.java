package com.soongsil.swcontest.controller;

import com.soongsil.swcontest.dto.request.GetPillTimeOnlyGuardianRequestDto;
import com.soongsil.swcontest.dto.request.RegisterPillTimeRequestDto;
import com.soongsil.swcontest.entity.Pill;
import com.soongsil.swcontest.security.AuthInfo;
import com.soongsil.swcontest.security.Authenticated;
import com.soongsil.swcontest.service.PillService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PillController {
    private final PillService pillService;

    @ApiOperation("약 등록하기(환자만 사용가능)")
    @PostMapping("/v1/registerPill")
    public List<Long> registerPillTime(@Authenticated AuthInfo authInfo, @Valid @RequestBody List<RegisterPillTimeRequestDto> registerPillTimeRequestDtos) {
        return pillService.registerPillTime(authInfo.getEmail(), registerPillTimeRequestDtos);
    }

    @ApiOperation("약 삭제하기(환자만 사용가능)")
    @PostMapping("/v1/deletePill")
    public void deletePillTime(@Authenticated AuthInfo authInfo, @RequestBody List<Long> deletePillIdList) {
        pillService.deletePillTime(authInfo.getEmail(), deletePillIdList);
    }

    @ApiOperation("환자본인이 먹는 약 가져오기(환자용)")
    @GetMapping("/v1/getPillProtege")
    public List<Pill> getPillTimeOnlyProtege(@Authenticated AuthInfo authInfo) {
        return pillService.getPillTimeOnlyProtege(authInfo.getEmail());
    }

    @ApiOperation("보호자가 등록한 환자가 먹는 약 가져오기(보호자용)")
    @GetMapping("/v1/getPillGuardian")
    public List<Pill> getPillTimeOnlyGuardian(@Authenticated AuthInfo authInfo, @Valid @RequestBody GetPillTimeOnlyGuardianRequestDto getPillTimeOnlyGuardianRequestDto) {
        return pillService.getPillTimeOnlyGuardian(authInfo.getEmail(), getPillTimeOnlyGuardianRequestDto.getProtegeEmail());
    }
}
