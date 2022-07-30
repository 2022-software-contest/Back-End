package com.soongsil.swcontest.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddProtegeRequestDto {
    @Email
    @NotBlank
    private String protegeEmail;

    @NotBlank
    private String phoneNumber;
}
