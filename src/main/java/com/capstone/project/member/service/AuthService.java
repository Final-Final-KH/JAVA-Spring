package com.capstone.project.member.service;

import com.capstone.project.jwt.TokenProvider;
import com.capstone.project.member.dto.TokenDto;
import com.capstone.project.member.dto.request.LoginRequestDto;
import com.capstone.project.member.dto.request.MemberRequestDto;
import com.capstone.project.member.dto.response.MemberResponseDto;
import com.capstone.project.member.entity.Member;
import com.capstone.project.member.repository.MemberRepository;
import com.capstone.project.oauth2.dto.GoogleLoginRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
                    LocalDateTime.now(ZoneId.of("Asia/Seoul")).plusDays(7) // 한국 시간대로 저장
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

    public TokenDto OAuth2Google(GoogleLoginRequestDto loginDto) {
        // 구글은 프로바이더 ID의 식별명이 sub 다. 하지만 프론트에서 프로바이더 id로 이름을 다시 붙여 보냈다.
        Optional<Member> existingMember = memberRepository.findByProviderAndProviderId(loginDto.getProvider(), loginDto.getProviderId());
        log.info(" 제 3자 로그인 요청 - 이메일: {}", loginDto.getEmail());
        Member member;
        if (existingMember.isPresent()) {
            //회원이 존재
            member = existingMember.get();
        } else {
            // 신규 회원
            member = new Member();
            member.setEmail(loginDto.getEmail());
            member.setName(loginDto.getName());
            member.setProvider(loginDto.getProvider());
            member.setProviderId(loginDto.getProviderId());
            memberRepository.save(member);
        }

        // 권한 정보 추가 (USER 권한 부여)
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("USER"));

        // Authentication 객체 생성 시 권한 추가
        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), null, authorities);

        // 토큰 발급
        TokenDto loginToken = tokenProvider.generateTokenDto(authentication);

        // 리프레시 토큰 저장
        try {
            tokenProvider.saveRefreshToken(
                    member,
                    loginToken.getRefreshToken(),
                    LocalDateTime.now(ZoneId.of("Asia/Seoul")).plusDays(7));
        } catch (Exception e) {
            log.error("리프레시 토큰 저장 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("리프레시 토큰 저장에 실패했습니다.");
        }
        return loginToken;

    }


}

