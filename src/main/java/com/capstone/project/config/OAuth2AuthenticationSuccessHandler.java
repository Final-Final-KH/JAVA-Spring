package com.capstone.project.config;

import com.capstone.project.member.dto.TokenDto;
import com.capstone.project.member.service.AuthService;
import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        Map<String, Object> attributes = token.getPrincipal().getAttributes();
        String provider = token.getAuthorizedClientRegistrationId();
        String providerId = attributes.get("sub").toString(); // Or "id" for Kakao
        String email = attributes.get("email").toString();
        String name = attributes.get("name").toString();

        TokenDto tokenDto = authService.loginWithOAuth2(provider, providerId, email, name);

        response.setHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
    }
}
