package com.soongsil.swcontest.exception.jwtException;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class HeaderHasNotAuthorization extends RuntimeException {
    private String message;
}
