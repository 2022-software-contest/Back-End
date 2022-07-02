package com.soongsil.swcontest.exception.userServiceException;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RefreshTokenImproperUseException extends RuntimeException {
    private String message;
}