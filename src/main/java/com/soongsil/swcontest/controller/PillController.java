package com.soongsil.swcontest.controller;

import com.soongsil.swcontest.dto.request.RegisterPillTimeRequestDto;
import com.soongsil.swcontest.security.AuthInfo;
import com.soongsil.swcontest.security.Authenticated;
import com.soongsil.swcontest.service.PillService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PillController {
    private final PillService pillService;

    @ApiOperation("약 등록하기")
    @PostMapping("/v1/registerPill")
    public List<Long> registerPillTime(@Authenticated AuthInfo authInfo, @Valid @RequestBody List<RegisterPillTimeRequestDto> registerPillTimeRequestDtos) {
        return pillService.registerPillTime(authInfo.getEmail(), registerPillTimeRequestDtos);
    }

    @ApiOperation("약 삭제하기(논의 후 수정 필요 환자로 로그인해야만 삭제가능?)")
    @PostMapping("/v1/deletePill")
    public void deletePillTime(@Authenticated AuthInfo authInfo, @RequestBody List<Long> deletePillIdList) {
        pillService.deletePillTime(authInfo.getEmail(), deletePillIdList);
    }
}
