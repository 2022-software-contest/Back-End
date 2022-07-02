package com.soongsil.swcontest.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soongsil.swcontest.exception.jwtException.HeaderHasNotAuthorization;
import com.soongsil.swcontest.exception.jwtException.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExceptionHandlerFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {
            filterChain.doFilter(request, response);
        } catch (InvalidTokenException exception) {
            setErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    request, response, exception.getMessage(), "토큰 오류 1번"
            );
        } catch (HeaderHasNotAuthorization exception) {
            setErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    request, response, exception.getMessage(), "토큰 오류 2번"
            );
        } catch (Exception exception) {
            setErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    request, response, exception.getMessage(), "정의되지 않은 모든 오류"
            );
        }
    }

    private void setErrorResponse(HttpStatus status,
                                  HttpServletRequest request,
                                  HttpServletResponse response,
                                  String exceptionMessage,
                                  String errorCode) {
        response.setStatus(status.value());
        response.setContentType("application/json");
        ErrorResponse errorResponse =
                new ErrorResponse(LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        errorCode, exceptionMessage, request.getRequestURI());
        try {
            log.warn("에러코드: " + errorCode + ", 요청 URI : " + request.getRequestURI() + ", 에러 메시지 : " + exceptionMessage);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
