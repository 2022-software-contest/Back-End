package com.soongsil.swcontest.exception.imageServiceException;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserDoesNotHaveImageException extends RuntimeException {
    private String message;
}
