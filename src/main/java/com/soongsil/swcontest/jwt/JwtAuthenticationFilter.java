package com.soongsil.swcontest.jwt;

import com.soongsil.swcontest.exception.jwtException.HeaderHasNotAuthorization;
import com.soongsil.swcontest.exception.jwtException.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    public static final String AUTHORIZATION_HEADER = "Authorization";

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (bearerToken==null) {
            throw new HeaderHasNotAuthorization("Authorization헤더에 토큰이 없습니다.");
        }
        String jwtToken = jwtTokenProvider.resolveToken(bearerToken);
        if(jwtToken != null &&
            jwtTokenProvider.validateToken(jwtToken)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(jwtToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        else {
            throw new InvalidTokenException("토큰이 유효하지 않습니다.");
        }
        filterChain.doFilter(request, response);
    }
}
