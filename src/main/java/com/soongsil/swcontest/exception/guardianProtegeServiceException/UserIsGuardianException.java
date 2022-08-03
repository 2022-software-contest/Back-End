package com.soongsil.swcontest.exception.guardianProtegeServiceException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserIsGuardianException extends RuntimeException {
    String message;
}
