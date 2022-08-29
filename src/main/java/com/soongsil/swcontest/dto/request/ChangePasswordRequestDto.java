package com.soongsil.swcontest.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChangePasswordRequestDto {
    @NotBlank
    private String oldPassword;
    @NotBlank
    private String newPassword;
}
