package com.soongsil.swcontest.dto.response;

import com.soongsil.swcontest.dto.dto.UserInfoDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class GetProtegesResponseDto {
    int protegesCount;
    List<UserInfoDto> protegesInfos;
}
