package com.soongsil.swcontest.exception;

import com.soongsil.swcontest.exception.imageServiceException.UploadFailException;
import com.soongsil.swcontest.exception.imageServiceException.UserDoesNotHaveImageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ImageServiceExceptionHandler {
    @ExceptionHandler(UploadFailException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUploadFailException(UploadFailException exception, HttpServletRequest request) {
        log.warn("에러코드 : 이미지서비스 오류 1번, 요청 URI : " + request.getRequestURI() + ", 에러 메시지 : " + exception.getMessage());
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "이미지서비스 오류 1번", exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(UserDoesNotHaveImageException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserDoesNotHaveImageException(UserDoesNotHaveImageException exception, HttpServletRequest request) {
        log.warn("에러코드 : 이미지서비스 오류 2번, 요청 URI : " + request.getRequestURI() + ", 에러 메시지 : " + exception.getMessage());
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "이미지서비스 오류 2번", exception.getMessage(), request.getRequestURI());
    }
}
