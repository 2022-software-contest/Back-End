package com.soongsil.swcontest.exception.guardianServiceException;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserIsNotGuardianException extends RuntimeException {
    String message;
}