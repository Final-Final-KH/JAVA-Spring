package com.capstone.project.oauth2.controller;

import com.capstone.project.member.dto.TokenDto;
import com.capstone.project.member.service.AuthService;
import com.capstone.project.member.service.MemberService;
import com.capstone.project.oauth2.dto.OAuth2LoginRequestDto;
import com.capstone.project.oauth2.service.NaverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/auth")
public class OAuth2Controller {

    private final AuthService authService;
    private final MemberService memberService;
    private final NaverService naverService;

    @PostMapping("/google")
    public ResponseEntity<TokenDto> googleLogin(@RequestBody OAuth2LoginRequestDto loginRequestDto) {
        System.out.println("Received request: " + loginRequestDto);
        System.out.println("Email: " + loginRequestDto.getEmail());
        TokenDto tokenDto = authService.OAuth2Login(loginRequestDto);
        return ResponseEntity.ok(tokenDto);
    }

    @PostMapping("/naver")
    public ResponseEntity<?> naverLogin(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        String state = request.get("state");
        System.out.println("네이버 로그인 시도중...");

        String accessToken = naverService.getNaverAccessToken(code, state);
        OAuth2LoginRequestDto loginDto = naverService.getUserInfo(accessToken);

        // 사용자 정보를 처리하거나 저장하는 로직
        log.info("네이버 사용자 정보: {}", loginDto.getName());

        TokenDto tokenDto = authService.OAuth2Login(loginDto);

        return ResponseEntity.ok(tokenDto);
    }
    @PostMapping("/kakao")
    public ResponseEntity<TokenDto> kakaoLogin(@RequestBody OAuth2LoginRequestDto loginRequestDto) {
        System.out.println("Received request: " + loginRequestDto);
        System.out.println("Email: " + loginRequestDto.getEmail());
        TokenDto tokenDto = authService.OAuth2Login(loginRequestDto);
        return ResponseEntity.ok(tokenDto);
    }

}
