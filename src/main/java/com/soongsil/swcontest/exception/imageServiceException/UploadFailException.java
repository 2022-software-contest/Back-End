package com.soongsil.swcontest.exception.imageServiceException;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UploadFailException extends RuntimeException {
    private String message;
}
