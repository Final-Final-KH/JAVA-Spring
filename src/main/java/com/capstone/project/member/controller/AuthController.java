package com.capstone.project.member.controller;

import com.capstone.project.member.dto.TokenDto;
import com.capstone.project.member.dto.request.LoginRequestDto;
import com.capstone.project.member.dto.request.MemberRequestDto;
import com.capstone.project.member.dto.response.MemberResponseDto;
import com.capstone.project.member.entity.Member;
import com.capstone.project.member.repository.MemberRepository;
import com.capstone.project.member.service.AuthService;
import com.capstone.project.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthService authService;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

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

    // Refresh token
    @PostMapping("/refresh")
    public ResponseEntity<TokenDto> refreshToken(@RequestBody String refreshToken) {
        TokenDto newTokens = authService.reissueAccessToken(refreshToken);
        return ResponseEntity.ok(newTokens);
    }

    // 토큰으로 유저 찾기
    @GetMapping("/current-user")
    public ResponseEntity<MemberResponseDto> getCurrentUser (Authentication authentication) {
        MemberResponseDto userDetails = memberService.getCurrentUser(Integer.valueOf(authentication.getName()));
        return ResponseEntity.ok(userDetails);
    }

    // 회원정보 수정
    @PutMapping("/current-user")
    public ResponseEntity<MemberResponseDto> updateUser(@RequestBody MemberRequestDto requestDto, Authentication authentication) {
        MemberResponseDto updatedMember = memberService.updateUser(authentication.getName(), requestDto);
        return ResponseEntity.ok(updatedMember);
    }

    // 사용자 권한 관리
    @PutMapping("/role")
    public ResponseEntity<Void> updateRole(@RequestParam String email, @RequestParam String role) {
        memberService.updateUserRole(email, role);
        return ResponseEntity.ok().build();
    }

    // AuthController.java
    @DeleteMapping("/current-user")
    public ResponseEntity<Void> deleteAccount(Authentication authentication) {
        memberService.deleteAccount(authentication.getName());
        return ResponseEntity.ok().build();
    }

    // 중복 이메일 회원 확인
    @PostMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email"); // 클라이언트에서 전달받은 이메일
        boolean isAvailable = !memberRepository.existsByEmail(email); // 이메일 사용 가능 여부

        Map<String, Boolean> response = new HashMap<>();
        response.put("isAvailable", isAvailable); // 결과를 JSON으로 반환
        return ResponseEntity.ok(response); // HTTP 상태 코드 200과 함께 반환
    }

    // 중복 전화번호 회원 확인
    @PostMapping("/check-phone")
    public ResponseEntity<Map<String, Boolean>> checkPhoneNumber(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phoneNumber");
        boolean isAvailable = !memberRepository.existsByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(Collections.singletonMap("isAvailable", isAvailable));
    }
}