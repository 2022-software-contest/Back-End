package com.soongsil.swcontest.exception;

import com.soongsil.swcontest.exception.guardianProtegeServiceException.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GuardianProtegeExceptionHandler {
    @ExceptionHandler(UserIsNotGuardianException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserIsNotGuardianException(UserIsNotGuardianException exception, HttpServletRequest request) {
        log.warn("에러코드 : 보호자-피보호자 오류 1번, 요청 URI : " + request.getRequestURI() + ", 에러 메시지 : " + exception.getMessage());
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "보호자-피보호자 오류 1번", exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(UserIsGuardianException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserIsGuardianException(UserIsGuardianException exception, HttpServletRequest request) {
        log.warn("에러코드 : 보호자-피보호자 오류 2번, 요청 URI : " + request.getRequestURI() + ", 에러 메시지 : " + exception.getMessage());
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "보호자-피보호자 오류 2번", exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(ProtegePhoneNumberNotEqualException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleProtegePhoneNumberNotEqualException(ProtegePhoneNumberNotEqualException exception, HttpServletRequest request) {
        log.warn("에러코드 : 보호자-피보호자 오류 3번, 요청 URI : " + request.getRequestURI() + ", 에러 메시지 : " + exception.getMessage());
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "보호자-피보호자 오류 3번", exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(ProtegeIsGuardianException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleProtegeIsGuardianException(ProtegeIsGuardianException exception, HttpServletRequest request) {
        log.warn("에러코드 : 보호자-피보호자 오류 4번, 요청 URI : " + request.getRequestURI() + ", 에러 메시지 : " + exception.getMessage());
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "보호자-피보호자 오류 4번", exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(ProtegeIsDuplicateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleProtegeIsDuplicateException(ProtegeIsDuplicateException exception, HttpServletRequest request) {
        log.warn("에러코드 : 보호자-피보호자 오류 5번, 요청 URI : " + request.getRequestURI() + ", 에러 메시지 : " + exception.getMessage());
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "보호자-피보호자 오류 5번", exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(GuardianHasNotProtegeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleGuardianHasNotProtegeException(GuardianHasNotProtegeException exception, HttpServletRequest request) {
        log.warn("에러코드 : 보호자-피보호자 오류 6번, 요청 URI : " + request.getRequestURI() + ", 에러 메시지 : " + exception.getMessage());
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "보호자-피보호자 오류 6번", exception.getMessage(), request.getRequestURI());
    }
}
