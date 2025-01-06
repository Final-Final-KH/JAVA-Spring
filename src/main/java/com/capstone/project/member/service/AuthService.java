package com.capstone.project.member.service;

import com.capstone.project.jwt.TokenProvider;
import com.capstone.project.member.dto.TokenDto;
import com.capstone.project.member.dto.request.LoginRequestDto;
import com.capstone.project.member.dto.request.MemberRequestDto;
import com.capstone.project.member.dto.response.MemberResponseDto;
import com.capstone.project.member.entity.Member;
import com.capstone.project.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    // User login and token issuance
    public TokenDto login(LoginRequestDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));

        return tokenProvider.generateTokenDto(authentication);
    }

    // Refresh access token
    public String refreshAccessToken(String refreshToken) {
        Authentication authentication = tokenProvider.getAuthentication(refreshToken);
        return tokenProvider.generateAccessToken(authentication);
    }

    // Register a new user
    public MemberResponseDto signUp(MemberRequestDto requestDto) {
        if (memberRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalStateException("Email already registered.");
        }

        Member member = Member.builder()
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .name(requestDto.getName())
                .phoneNumber(requestDto.getPhoneNumber())
                .currentCompany("Unemployed")
                .showCompany(false)
                .build();

        Member savedMember = memberRepository.save(member);
        return new MemberResponseDto(savedMember);
    }
}
