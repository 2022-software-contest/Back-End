package com.soongsil.swcontest.exception.guardianServiceException;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProtegePhoneNumberNotEqualException extends RuntimeException {
    String message;
}
