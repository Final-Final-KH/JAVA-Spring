package com.capstone.project.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    // Check if the email is already registered
    public boolean isEmailRegistered(String email) {
        return memberRepository.existsByEmail(email);
    }

    // Register a new user
    public MemberResponseDto signUp(MemberRequestDto requestDto) {
        if (memberRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalStateException("이미 등록된 Email 입니다.");
        }

        Member member = Member.builder()
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .name(requestDto.getName())
                .phoneNumber(requestDto.getPhoneNumber())
                .currentCompany("Unemployed") // Default value
                .showCompany(false)           // Default visibility
                .build();

        Member savedMember = memberRepository.save(member);
        return new MemberResponseDto(savedMember);
    }

    // Authenticate and issue tokens
    public TokenDto login(LoginRequestDto loginDto) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        return tokenProvider.generateTokenDto(authentication);
    }

    // Refresh access token
    public String createAccessToken(String refreshToken) {
        Authentication authentication = tokenProvider.getAuthentication(refreshToken);
        return tokenProvider.generateAccessToken(authentication);
    }
}
