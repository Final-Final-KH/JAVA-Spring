package com.capstone.project.oauth2.service;

import com.capstone.project.oauth2.dto.OAuth2LoginRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j

public class GitHubService {
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String CLIENT_ID = "YOUR_GITHUB_CLIENT_ID";
    private static final String CLIENT_SECRET = "YOUR_GITHUB_CLIENT_SECRET";
    private static final String TOKEN_URL = "https://github.com/login/oauth/access_token";
    private static final String USER_INFO_URL = "https://api.github.com/user";

    public String getGitHubAccessToken(String code) {
        Map<String, String> params = new HashMap<>();
        params.put("client_id", CLIENT_ID);
        params.put("client_secret", CLIENT_SECRET);
        params.put("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                TOKEN_URL,
                HttpMethod.POST,
                requestEntity,
                Map.class
        );

        if (response.getBody() != null && response.getBody().containsKey("access_token")) {
            return response.getBody().get("access_token").toString();
        } else {
            throw new RuntimeException("GitHub OAuth 토큰을 가져오는 데 실패했습니다.");
        }
    }

    public OAuth2LoginRequestDto getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                USER_INFO_URL,
                HttpMethod.GET,
                requestEntity,
                Map.class
        );

        if (response.getBody() != null) {
            Map<String, Object> userInfo = response.getBody();
            String email = (String) userInfo.get("email");  // GitHub은 기본적으로 이메일 제공 X
            String name = (String) userInfo.get("name");
            String login = (String) userInfo.get("login");

            if (email == null) {
                email = login + "@github.com";  // GitHub 이메일이 비어있다면 가짜 이메일 생성
            }

            // provider와 providerId 추가
            String provider = "github";  // GitHub을 고정
            String providerId = login;  // GitHub 로그인 ID

            return new OAuth2LoginRequestDto(email, name, provider, providerId);
        } else {
            throw new RuntimeException("GitHub 사용자 정보를 가져오는 데 실패했습니다.");
        }
    }
}
