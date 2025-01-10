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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

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
                .currentCompany(requestDto.getCurrentCompany()) // Default value
                .showCompany(requestDto.getShowCompany())           // Default visibility
                .build();

        Member savedMember = memberRepository.save(member);
        return new MemberResponseDto(savedMember);
    }

    // Authenticate and issue tokens
    public TokenDto login(LoginRequestDto loginDto) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());
        log.info("로그인 요청 - 이메일: {}", loginDto.getEmail());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        TokenDto loginToken = tokenProvider.generateTokenDto(authentication);

        // 이메일을 사용해 Member를 데이터베이스에서 조회
        Member member = memberRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));
        try {
            tokenProvider.saveRefreshToken(
                    member,
                    loginToken.getRefreshToken(),
                    LocalDateTime.now(ZoneId.of("UTC")).plusDays(7) // UTC 시간대로 고정
            );
        } catch (Exception e) {
            log.error("리프레시 토큰 저장 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("리프레시 토큰 저장에 실패했습니다.");
        }
        return loginToken;
    }

    // Refresh access token
    public String createAccessToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        // 리프레시 토큰으로부터 사용자 정보 추출
        Authentication authentication = tokenProvider.getAuthentication(refreshToken);

        // 새 액세스 토큰 발급
        return tokenProvider.generateAccessToken(authentication);
    }

    public TokenDto loginWithOAuth2(String provider, String providerId, String email, String name) {
        Member member = memberRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> {
                    // Register new OAuth2 user
                    Member newMember = Member.builder()
                            .email(email)
                            .name(name)
                            .provider(provider)
                            .providerId(providerId)
                            .role(Member.Role.USER)
                            .build();
                    return memberRepository.save(newMember);
                });

        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getEmail(), null, List.of());
        return tokenProvider.generateTokenDto(authentication);
    }

}

