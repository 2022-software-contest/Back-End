package com.soongsil.swcontest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddProtegeResponseDto {
    String guardianEmail;
    String protegeEmail;
}
