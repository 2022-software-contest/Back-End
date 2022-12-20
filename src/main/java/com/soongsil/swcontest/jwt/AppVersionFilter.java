package com.soongsil.swcontest.jwt;

import com.soongsil.swcontest.exception.jwtException.HeaderHasNotAuthorization;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class AppVersionFilter extends OncePerRequestFilter {
    public static final String VERSION_HEADER = "Version";
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String version = request.getHeader(VERSION_HEADER);
        System.out.println(version);
        if (version==null) {
            throw new HeaderHasNotAuthorization("알랄랄라");
        }
        filterChain.doFilter(request, response);
    }
}
