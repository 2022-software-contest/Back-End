package com.soongsil.swcontest.controller;

import com.soongsil.swcontest.dto.request.FirebaseRequestDto;
import com.soongsil.swcontest.service.FirebaseCloudMessageService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FirebaseController {
    private final FirebaseCloudMessageService firebaseCloudMessageService;

    @PostMapping("/v1/push")
    @ApiOperation("푸시알림 확인 용도(테스트용도 로그인X)")
    public void pushMessage(@RequestBody FirebaseRequestDto testDto) {
        firebaseCloudMessageService.sendMessageTo(testDto.getTargetToken(), testDto.getTitle(), testDto.getBody(), testDto.getUsage());
    }
}
