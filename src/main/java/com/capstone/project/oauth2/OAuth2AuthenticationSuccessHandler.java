package com.capstone.project.oauth2;

import com.capstone.project.jwt.TokenProvider;
import com.capstone.project.member.dto.TokenDto;
import com.capstone.project.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final OAuth2Service oAuth2Service;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;

        // Extract OAuth2 attributes
        Map<String, Object> attributes = token.getPrincipal().getAttributes();
        String provider = token.getAuthorizedClientRegistrationId();
        String providerId = attributes.get("sub").toString(); // 카카오에서는 "id" 로 찾아야..
        String email = attributes.get("email").toString();
        String name = attributes.get("name").toString();

        // 유저를 찾거나 DB에 등록.. (Find or register the user in the database )
        Member member = oAuth2Service.findOrRegisterOAuth2User(provider, providerId, email, name);

        // 유저를 위해 OAUTH2 토큰 생성
        TokenDto tokenDto = oAuth2Service.generateTokensForMember(member);

        // 클라이언트에 토큰 반환
        response.setHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
    }
}
