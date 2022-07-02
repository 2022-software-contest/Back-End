package com.soongsil.swcontest.security;

import com.soongsil.swcontest.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class AuthInfo {
    private String token;
    private String email;
    private List<RoleType> role;
}
