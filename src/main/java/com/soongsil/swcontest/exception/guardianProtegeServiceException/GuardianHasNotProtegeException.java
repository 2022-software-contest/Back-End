package com.soongsil.swcontest.exception.guardianProtegeServiceException;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GuardianHasNotProtegeException extends RuntimeException {
    String message;
}
