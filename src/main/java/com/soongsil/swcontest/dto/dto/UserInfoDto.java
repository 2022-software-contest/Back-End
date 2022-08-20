package com.soongsil.swcontest.dto.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserInfoDto {
    private Long id;
    private String email;
    private String username;
    private String phoneNumber;
    private Boolean isGuardian;
}
