package com.capstone.project.config;

import com.capstone.project.jwt.TokenProvider;
import com.capstone.project.oauth2.CustomOAuth2UserService;
import com.capstone.project.security.JwtAuthenticationEntryPoint;
import com.capstone.project.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtFilter jwtFilter; // JwtFilter is injected here / JwtFilter가 여기에 주입.
    private final JwtAuthenticationEntryPoint authenticationEntryPoint; // Handles unauthorized requests / 인증되지 않은 요청 처리
    private final CustomOAuth2UserService customOAuth2UserService; // Handles OAuth2 user details / OAuth2 사용자 세부정보 처리

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Provides password encryption / 비밀번호 암호화를 제공.
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .antMatchers("/api/auth/**", "/oauth2/**").permitAll() // Allow access to these endpoints / 이 엔드포인트에 대한 접근 허용
                        .anyRequest().authenticated() // All other endpoints require authentication / 나머지 엔드포인트는 인증이 필요
                )
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint) // Handle exceptions for unauthorized requests / 인증되지 않은 요청에 대한 예외 처리
                .and()
                .addFilterBefore(jwtFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class) // Add JwtFilter before UsernamePasswordAuthenticationFilter / JwtFilter를 UsernamePasswordAuthenticationFilter 전에 추가
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/api/auth/login") // Define login page for OAuth2 / OAuth2 로그인 페이지 정의
                        .userInfoEndpoint()
                        .userService(customOAuth2UserService) // Use CustomOAuth2UserService to process OAuth2 user details / CustomOAuth2UserService를 사용하여 OAuth2 사용자 정보 처리
                );

        return http.build(); // Build the security configuration / 보안 구성을 빌드
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager(); // Provide the AuthenticationManager bean / AuthenticationManager 빈 제공
    }
}
