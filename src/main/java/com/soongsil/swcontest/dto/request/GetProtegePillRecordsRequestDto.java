package com.soongsil.swcontest.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class GetProtegePillRecordsRequestDto {
    @NotBlank
    @Email
    String email;
}
