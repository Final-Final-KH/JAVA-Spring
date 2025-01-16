package com.capstone.project.oauth2.service;

import com.capstone.project.oauth2.dto.OAuth2LoginRequestDto;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@Slf4j
public class NaverService {

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String clientSecret;

    public String getNaverAccessToken(String code, String state) {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://nid.naver.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();

        // 토큰 요청
        JSONObject response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/oauth2.0/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", clientId)
                        .queryParam("client_secret", clientSecret)
                        .queryParam("code", code)
                        .queryParam("state", state)
                        .build())
                .retrieve()
                .bodyToMono(JSONObject.class)
                .block();

        return response.getAsString("access_token");
    }

    public OAuth2LoginRequestDto getUserInfo(String accessToken) {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://openapi.naver.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build();

        JSONObject response = webClient.get()
                .uri("/v1/nid/me")
                .retrieve()
                .bodyToMono(JSONObject.class)
                .block();

        // JSON 응답에서 필요한 데이터 추출
        Map<String, Object> userInfo = (Map<String, Object>) response.get("response");

        OAuth2LoginRequestDto dto = new OAuth2LoginRequestDto();
        dto.setEmail((String) userInfo.get("email")); // email
        dto.setName((String) userInfo.get("name"));  // name
        dto.setProvider("naver");                   // provider
        dto.setProviderId((String) userInfo.get("id")); // providerId

        return dto;
    }

}
