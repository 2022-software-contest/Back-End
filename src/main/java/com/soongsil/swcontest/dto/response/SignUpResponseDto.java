package com.soongsil.swcontest.dto.response;

import com.soongsil.swcontest.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SignUpResponseDto {
    private String email;
    private String username;
    private RoleType role;
}
