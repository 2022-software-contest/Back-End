package com.soongsil.swcontest.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class GetPillTimeOnlyGuardianRequestDto {
    @Email
    private String protegeEmail;
}
