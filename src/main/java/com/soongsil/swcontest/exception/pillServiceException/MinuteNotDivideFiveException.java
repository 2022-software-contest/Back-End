package com.soongsil.swcontest.exception.pillServiceException;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MinuteNotDivideFiveException extends RuntimeException {
    private String message;
}
