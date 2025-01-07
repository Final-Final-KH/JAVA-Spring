package com.capstone.project.security;

import com.capstone.project.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component // Register this filter as a Spring bean / 이 필터를 스프링 빈으로 등록
public class JwtFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider; // Handles JWT validation and authentication / JWT 검증 및 인증 처리

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Extract JWT from the request / 요청에서 JWT 추출
        String jwt = resolveToken(request);

        // Validate the token and set the authentication object / 토큰을 검증하고 인증 객체 설정
        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            Authentication authentication = tokenProvider.getAuthentication(jwt); // Authenticate user / 사용자 인증
            SecurityContextHolder.getContext().setAuthentication(authentication); // Set authentication in the SecurityContext / SecurityContext에 인증 설정
        }

        filterChain.doFilter(request, response); // Pass the request to the next filter / 요청을 다음 필터로 전달
    }

    // Authorization 헤더에서 토큰 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization"); // Get Authorization header / Authorization 헤더 가져오기
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remove the "Bearer " prefix / "Bearer " 접두사 제거
        }
        return null; // Return null if token is not present / 토큰이 없으면 null 반환
    }
}

