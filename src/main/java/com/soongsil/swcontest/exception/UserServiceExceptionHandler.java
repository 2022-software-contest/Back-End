package com.soongsil.swcontest.exception;

import com.soongsil.swcontest.exception.userServiceException.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class UserServiceExceptionHandler {
    @ExceptionHandler(ExistsEmailException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidStationNameException(ExistsEmailException exception, HttpServletRequest request) {
        log.warn("에러코드 : 유저서비스 오류 1번, 요청 URI : " + request.getRequestURI() + ", 에러 메시지 : " + exception.getMessage());
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "유저서비스 오류 1번", exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(PasswordIncorrectException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlePasswordInCorrectException(PasswordIncorrectException exception, HttpServletRequest request) {
        log.warn("에러코드 : 유저서비스 오류 2번, 요청 URI : " + request.getRequestURI() + ", 에러 메시지 : " + exception.getMessage());
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "유저서비스 오류 2번", exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(RefreshTokenNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleRefreshTokenNotFoundException(RefreshTokenNotFoundException exception, HttpServletRequest request) {
        log.warn("에러코드 : 유저서비스 오류 3번, 요청 URI : " + request.getRequestURI() + ", 에러 메시지 : " + exception.getMessage());
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "유저서비스 오류 3번", exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException exception, HttpServletRequest request) {
        log.warn("에러코드 : 유저서비스 오류 4번, 요청 URI : " + request.getRequestURI() + ", 에러 메시지 : " + exception.getMessage());
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "유저서비스 오류 4번", exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(RefreshTokenImproperUseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleRefreshTokenImproperUseException(RefreshTokenImproperUseException exception, HttpServletRequest request) {
        log.warn("에러코드 : 유저서비스 오류 5번, 요청 URI : " + request.getRequestURI() + ", 에러 메시지 : " + exception.getMessage());
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "유저서비스 오류 5번", exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(AlreadyLogoutException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleAlreadyLogoutException(AlreadyLogoutException exception, HttpServletRequest request) {
        log.warn("에러코드 : 유저서비스 오류 6번, 요청 URI : " + request.getRequestURI() + ", 에러 메시지 : " + exception.getMessage());
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "유저서비스 오류 6번", exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(OldPasswordEqualsNewPasswordException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleOldPasswordEqualsNewPasswordException(OldPasswordEqualsNewPasswordException exception, HttpServletRequest request) {
        log.warn("에러코드 : 유저서비스 오류 7번, 요청 URI : " + request.getRequestURI() + ", 에러 메시지 : " + exception.getMessage());
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "유저서비스 오류 7번", exception.getMessage(), request.getRequestURI());
    }
}
