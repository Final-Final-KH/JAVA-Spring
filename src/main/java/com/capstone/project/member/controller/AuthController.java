package com.capstone.project.member.controller;

import com.capstone.project.member.dto.TokenDto;
import com.capstone.project.member.dto.request.LoginRequestDto;
import com.capstone.project.member.dto.request.MemberRequestDto;
import com.capstone.project.member.dto.response.MemberResponseDto;
import com.capstone.project.member.service.AuthService;
import com.capstone.project.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthService authService;
    private final MemberService memberService;

    // Register a new user
    @PostMapping("/signup")
    public ResponseEntity<MemberResponseDto> signUp(@RequestBody MemberRequestDto requestDto) {
        MemberResponseDto registeredMember = authService.signUp(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredMember);
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody LoginRequestDto loginDto) {
        TokenDto tokenDto = authService.login(loginDto);
        return ResponseEntity.ok(tokenDto);
    }

    // Refresh token
    @PostMapping("/refresh")
    public ResponseEntity<String> refreshToken(@RequestBody String refreshToken) {
        String newAccessToken = authService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(newAccessToken);
    }
}
