package com.soongsil.swcontest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ReissueResponseDto {
    private String email;
    private String accessToken;
    private String refreshToken;
}
