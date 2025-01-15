package com.capstone.project.oauth2.controller;

import com.capstone.project.member.dto.TokenDto;
import com.capstone.project.member.service.AuthService;
import com.capstone.project.member.service.MemberService;
import com.capstone.project.oauth2.dto.GoogleLoginRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/auth")
public class OAuth2Controller {

    private final AuthService authService;
    private final MemberService memberService;

    @PostMapping("/google")
    public ResponseEntity<TokenDto> googleLogin(@RequestBody GoogleLoginRequestDto loginRequestDto) {
        System.out.println(loginRequestDto.getEmail());
        TokenDto tokenDto = authService.OAuth2Google(loginRequestDto);
        return ResponseEntity.ok(tokenDto);
    }
}
