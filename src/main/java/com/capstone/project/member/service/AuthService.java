package com.capstone.project.member.service;

import com.capstone.project.jwt.TokenProvider;
import com.capstone.project.jwt.entity.RefreshToken;
import com.capstone.project.jwt.repository.RefreshTokenRepository;
import com.capstone.project.member.dto.TokenDto;
import com.capstone.project.member.dto.request.LoginRequestDto;
import com.capstone.project.member.dto.request.MemberRequestDto;
import com.capstone.project.member.dto.response.MemberResponseDto;
import com.capstone.project.member.entity.Member;
import com.capstone.project.member.repository.MemberRepository;
import com.capstone.project.oauth2.dto.OAuth2LoginRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 인증 서비스 클래스
 * <p>
 * 회원 가입, 로그인, 토큰 재발급 등 인증 관련 로직을 처리합니다.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    /**
     * 이메일 등록 여부 확인
     *
     * @param email 확인할 이메일
     * @return true: 등록됨, false: 미등록
     */
    public boolean isEmailRegistered(String email) {
        return memberRepository.existsByEmail(email);
    }

    /**
     * 회원 가입
     *
     * @param requestDto 회원 가입 요청 DTO
     * @return 가입된 회원 정보 DTO
     * @throws IllegalStateException 이미 등록된 이메일일 경우 예외 발생
     */
    public MemberResponseDto signUp(MemberRequestDto requestDto) {
        if (memberRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalStateException("이미 등록된 Email 입니다.");
        }

        Member member = Member.builder()
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .name(requestDto.getName())
                .phoneNumber(requestDto.getPhoneNumber())
                .currentCompany(requestDto.getCurrentCompany()) // 기본값 사용
                .showCompany(requestDto.getShowCompany())         // 공개 여부
                .build();

        Member savedMember = memberRepository.save(member);
        return new MemberResponseDto(savedMember);
    }

    /**
     * 로그인 및 토큰 발급
     *
     * @param loginDto 로그인 요청 DTO
     * @return 발급된 토큰 DTO
     * @throws RuntimeException 회원 정보를 찾을 수 없거나 리프레시 토큰 저장에 실패할 경우 예외 발생
     */
    public TokenDto login(LoginRequestDto loginDto) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());
        log.info("로그인 요청 - 이메일: {}", loginDto.getEmail());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        TokenDto loginToken = tokenProvider.generateTokenDto(authentication);

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

    /**
     * 리프레시 토큰을 사용하여 새 액세스 토큰 생성
     *
     * @param refreshToken 리프레시 토큰
     * @return 새 액세스 토큰 문자열
     * @throws IllegalArgumentException 유효하지 않은 토큰일 경우 예외 발생
     */
    public String createAccessToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        // 리프레시 토큰으로부터 사용자 정보 추출
        Authentication authentication = tokenProvider.getAuthentication(refreshToken);

        // 새 액세스 토큰 발급
        return tokenProvider.generateAccessToken(authentication);
    }

    /**
     * OAuth2 로그인 처리
     *
     * @param loginDto OAuth2 로그인 요청 DTO
     * @return 발급된 토큰 DTO
     */
    public TokenDto OAuth2Login(OAuth2LoginRequestDto loginDto) {
        // 구글은 프로바이더 ID의 식별명이 sub 다. 하지만 프론트에서 프로바이더 id로 이름을 다시 붙여 보냈다.
        // 네이버는 프로바이더 ID의 식별명이 ID 다. 이것 또한 프론트에서 프로바이더 id로 이름을 다시 붙여 보냈다.
        Optional<Member> existingMember = memberRepository.findByProviderAndProviderId(loginDto.getProvider(), loginDto.getProviderId());
        log.info("제 3자 로그인 요청 - 이메일: {}", loginDto.getEmail());
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

    /**
     * 리프레시 토큰을 사용하여 토큰 재발급
     *
     * @param refreshToken 리프레시 토큰
     * @return 새 토큰 DTO
     * @throws IllegalArgumentException 유효하지 않거나 존재하지 않는 토큰일 경우 예외 발생
     */
    public TokenDto reissueAccessToken(String refreshToken) {
        log.info("토큰 재발급 요청 - 리프레시 토큰: {}", refreshToken);


        // 리프레시 토큰 유효성 검사
        if (!tokenProvider.validateToken(refreshToken)) {
            log.error("유효하지 않은 리프레시 토큰입니다.");
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰");
        }
        // DB에서 리프레시 토큰 찾기
        RefreshToken storedToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(()-> new IllegalArgumentException("리프레시 토큰이 존재하지 않습니다."));

        // 토큰에 해당하는 맴버 추출
        Member member = storedToken.getMember();
        log.info("리프레시 토큰에 해당하는 사용자 ID: {}", member.getMemberId());

        // 새 토큰 발급
        Authentication authentication = tokenProvider.getAuthentication(refreshToken);
        TokenDto newToken = tokenProvider.generateTokenDto(authentication);

        // 새 리프레시 토큰 저장
        tokenProvider.saveRefreshToken(member, newToken.getRefreshToken(),
                LocalDateTime.now(ZoneId.of("Asia/Seoul")).plusDays(7));

        return newToken;
    }
}
