package com.soongsil.swcontest.exception.guardianProtegeServiceException;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserIsNotGuardianException extends RuntimeException {
    String message;
}
