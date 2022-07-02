package com.soongsil.swcontest.exception.jwtException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HeaderHasNotAuthorization extends RuntimeException {
    private String message;
}
