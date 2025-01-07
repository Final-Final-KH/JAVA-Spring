package com.capstone.project.member.controller;

import com.capstone.project.member.dto.TokenDto;
import com.capstone.project.member.dto.request.LoginRequestDto;
import com.capstone.project.member.dto.request.MemberRequestDto;
import com.capstone.project.member.dto.response.MemberResponseDto;
import com.capstone.project.member.service.AuthService;
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

    // Check if the email is already registered
    @GetMapping("/exists/{email}")
    public ResponseEntity<Boolean> isEmailRegistered(@PathVariable String email) {
        boolean exists = authService.isEmailRegistered(email);
        return ResponseEntity.ok(exists);
    }

    // Register a new user
    @PostMapping("/signup")
    public ResponseEntity<MemberResponseDto> signUp(@RequestBody MemberRequestDto requestDto) {
        MemberResponseDto registeredMember = authService.signUp(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredMember);
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody LoginRequestDto loginDto) {
        System.out.println(loginDto.getEmail());
        TokenDto tokenDto = authService.login(loginDto);
        return ResponseEntity.ok(tokenDto);
    }

    // Refresh access token
    @PostMapping("/refresh")
    public ResponseEntity<String> refreshToken(@RequestBody String refreshToken) {
        String newAccessToken = authService.createAccessToken(refreshToken);
        return ResponseEntity.ok(newAccessToken);
    }
}