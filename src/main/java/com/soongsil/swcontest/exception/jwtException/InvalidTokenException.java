package com.soongsil.swcontest.exception.jwtException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InvalidTokenException extends RuntimeException{
    private String message;
}
