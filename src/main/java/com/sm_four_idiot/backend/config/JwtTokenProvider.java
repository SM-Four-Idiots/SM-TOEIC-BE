package com.sm_four_idiot.backend.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 토큰 생성, 검증, 파싱을 담당하는 클래스
 */
@Component
public class JwtTokenProvider {

    /** JWT 서명에 사용할 비밀키 (application.yaml에서 주입) */
    private final SecretKey secretKey;

    /** 액세스 토큰 만료 시간 (밀리초) */
    private final long accessTokenExpiration;

    /** 리프레시 토큰 만료 시간 (밀리초) */
    private final long refreshTokenExpiration;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-expiration}") long refreshTokenExpiration) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    /**
     * JWT 액세스 토큰 생성
     * @param email 사용자 이메일 (subject)
     * @param role  사용자 권한 (USER / ADMIN)
     * @return 생성된 JWT 토큰 문자열
     */
    public String generateToken(String email, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 리프레시 토큰 생성
     * @param email 사용자 이메일
     * @return 생성된 리프레시 토큰 문자열
     */
    public String generateRefreshToken(String email) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenExpiration);

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 토큰에서 이메일(subject) 추출
     * @param token JWT 토큰
     * @return 이메일
     */
    public String getEmail(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * 토큰에서 권한 추출
     * @param token JWT 토큰
     * @return 권한 문자열 (USER / ADMIN)
     */
    public String getRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    /**
     * 토큰 유효성 검증
     * @param token JWT 토큰
     * @return 유효하면 true, 만료/변조 등 이상이 있으면 false
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 토큰 파싱 (서명 검증 포함)
     * @param token JWT 토큰
     * @return Claims (토큰 내 데이터)
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}