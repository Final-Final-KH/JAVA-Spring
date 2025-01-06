package com.capstone.project.jwt;

import com.capstone.project.member.dto.TokenDto;
import com.capstone.project.member.entity.Member;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

// JWT-related operations: creation, validation, and extraction.
// JWT 생성, 검증 및 추출 관련 작업을 수행.

@Component // 스프링 빈으로 등록
@Slf4j
public class TokenProvider {

    private static final String AUTHORITIES_KEY = "auth"; // Key to store authority information
    // 권한 정보를 저장할 키
    private final Key key; // Secret key used for signing JWTs
    // JWT 서명에 사용할 비밀 키

    public TokenProvider(@Value("${jwt.secret}") String secretKey) {
        // Initialize the key using the provided secret
        // 제공된 비밀키를 사용해 키를 초기화.
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // Generate both access and refresh tokens
    // Access 및 Refresh 토큰을 생성.
    public TokenDto generateTokenDto(Authentication authentication) {
        // Extract authority information from the authentication object
        // 인증 객체에서 권한 정보를 추출.
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        // Access token valid for 30 minutes
        // Access 토큰은 30분 동안 유효.
        Date accessTokenExpiresIn = new Date(now + 30 * 60 * 1000);

        // Refresh token valid for 6 days
        // Refresh 토큰은 6일 동안 유효.
        Date refreshTokenExpiresIn = new Date(now + 30 * 60 * 1000 * 48 * 6);

        // Create Access Token
        // Access 토큰 생성
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName()) // Set the username
                .claim(AUTHORITIES_KEY, authorities)  // Store authority information
                .setExpiration(accessTokenExpiresIn)  // Set expiration time
                .signWith(key, SignatureAlgorithm.HS512) // Sign the token
                .compact();

        // Create Refresh Token
        // Refresh 토큰 생성
        String refreshToken = Jwts.builder()
                .setSubject(authentication.getName()) // Set the username
                .setExpiration(refreshTokenExpiresIn) // Set expiration time
                .signWith(key, SignatureAlgorithm.HS512) // Sign the token
                .compact();

        // Return token data as a DTO
        // 토큰 데이터를 DTO로 반환
        return TokenDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .refreshToken(refreshToken)
                .refreshTokenExpiresIn(refreshTokenExpiresIn.getTime())
                .build();
    }

    // Extract Authentication object from a token
    // 토큰에서 Authentication 객체를 추출합니다.
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        // Extract authority information
        // 권한 정보를 추출.
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // Create a User object
        // User 객체 생성
        User principal = new User(claims.getSubject(), "", authorities);

        // Return an Authentication object
        // Authentication 객체 반환
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    // Validate the token
    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature.");
            // 잘못된 JWT 서명입니다.
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
            // 만료된 JWT 토큰입니다.
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
            // 지원되지 않는 JWT 토큰입니다.
        } catch (IllegalArgumentException e) {
            log.info("JWT token is invalid.");
            // JWT 토큰이 잘못되었습니다.
        }
        return false;
    }

    // Parse the token to extract claims
    // 토큰에서 클레임 정보를 추출.
    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    // Generate only an access token
    // Access 토큰만 생성
    public String generateAccessToken(Authentication authentication) {
        return generateTokenDto(authentication).getAccessToken();
    }

    // Generate an Authentication object from a Member entity
    // Member 엔터티에서 Authentication 객체 생성
    public Authentication getAuthenticationFromMember(Member member) {
        // Create authorities (roles) for the user
        // 사용자 권한(역할)을 생성
        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(member.getRole().name()));

        // Create a User object with member details
        // Member 세부 정보를 가진 User 객체 생성
        User principal = new User(member.getEmail(), "", authorities);

        // Return an Authentication object
        // Authentication 객체 반환
        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }
}
