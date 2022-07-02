package com.soongsil.swcontest.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soongsil.swcontest.exception.jwtException.HeaderHasNotAuthorization;
import com.soongsil.swcontest.exception.jwtException.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ExceptionHandlerFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
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
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
