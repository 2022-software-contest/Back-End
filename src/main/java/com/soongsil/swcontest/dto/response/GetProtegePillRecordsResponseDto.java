package com.soongsil.swcontest.dto.response;

import com.soongsil.swcontest.entity.Pill;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetProtegePillRecordsResponseDto {
    private int pillCount;
    private List<Pill> pills;
}
