package com.soongsil.swcontest.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RegisterTokenRequestDto {
    @NotBlank
    private String token;
}
