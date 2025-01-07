package com.capstone.project.config;

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
    private final JwtFilter jwtFilter; // JwtFilter 주입
    private final JwtAuthenticationEntryPoint authenticationEntryPoint; // 인증되지 않은 요청 처리
    private final CustomOAuth2UserService customOAuth2UserService; // OAuth2 사용자 정보 처리

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 비밀번호 암호화 제공
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .antMatchers(
                                "/auth/**",
                                "/oauth2/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/v2/api-docs",
                                "/webjars/**"
                        ).permitAll() // 해당 엔드포인트 접근 허용
                        .anyRequest().authenticated() // 나머지 엔드포인트는 인증 필요
                )
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint) // 인증되지 않은 요청에 대한 예외 처리
                .and()
                .addFilterBefore(jwtFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class) // JwtFilter 추가
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/api/auth/login") // OAuth2 로그인 페이지
                        .userInfoEndpoint()
                        .userService(customOAuth2UserService) // OAuth2 사용자 정보 처리
                );

        return http.build(); // 보안 구성을 빌드
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager(); // AuthenticationManager 빈 제공
    }
}
