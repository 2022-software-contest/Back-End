package com.soongsil.swcontest.jwt;

import com.soongsil.swcontest.enums.RoleType;
import com.soongsil.swcontest.exception.jwtException.InvalidTokenException;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.naming.AuthenticationException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {
    private final long ACCESS_TOKEN_VALID_TIME = (1000 * 60 * 60 * 24); // 하루
    private final long REFRESH_TOKEN_VALID_TIME = (1000 * 60 * 60 * 24 * 7); // 일주일
    private final String BEARER_TYPE = "Bearer ";
    @Value("jwt.secret")
    private String secretKey;

    public TokenInfo createJwtAccessToken(String email) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + ACCESS_TOKEN_VALID_TIME);
        String accessToken = Jwts.builder()
                .claim("email", email)
                .claim("ROLE_", RoleType.USER)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        accessToken = BEARER_TYPE + accessToken;
        return new TokenInfo(accessToken, expiration.getTime() - now.getTime());
    }

    public TokenInfo createJwtRefreshToken(String email) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + REFRESH_TOKEN_VALID_TIME);
        String refreshToken = Jwts.builder()
                .claim("email", email)
                .claim("ROLE_", RoleType.USER)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        refreshToken = BEARER_TYPE + refreshToken;
        return new TokenInfo(refreshToken, expiration.getTime() - now.getTime());
    }

    public Boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (SecurityException e) {
            throw new InvalidTokenException("JWT에러 Security");
        } catch (MalformedJwtException e) {
            throw new InvalidTokenException("JWT에러 Malformed");
        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            throw new InvalidTokenException("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            throw new InvalidTokenException("잘못된 JWT 헤더 입니다.");
        } catch (SignatureException e) {
            throw new InvalidTokenException("잘못된 JWT 서명 입니다.");
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        try {
            claims.get("email");
        } catch (Exception e) {
            try {
                throw new AuthenticationException("JWT 토큰에 email이 없습니다.");
            } catch (AuthenticationException ex) {
                ex.printStackTrace();
            }
        }
        Collection<GrantedAuthority> authorities =
                Arrays.stream(claims.get("ROLE_").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        UserDetails userDetails = new User(claims.get("email").toString(), "", authorities);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public Claims parseClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    public Long getExpiration(String token) {
        Date expiration = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(resolveToken(token)).getBody().getExpiration();
        Date now = new Date();
        return expiration.getTime() - now.getTime();
    }

    public String resolveToken(String token) {
        if (token.startsWith(BEARER_TYPE)) {
            return token.replace("Bearer ", "");
        }
        return null;
    }
}
