// 추후 사용

//package com.capstone.project.oauth2;
//
//import com.capstone.project.jwt.TokenProvider;
//import com.capstone.project.member.dto.TokenDto;
//import com.capstone.project.member.entity.Member;
//import com.capstone.project.member.repository.MemberRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class OAuth2Service {
//
//    private final MemberRepository memberRepository;
//    private final TokenProvider tokenProvider;
//
//    // Generate tokens for an authenticated user
//    public TokenDto generateTokensForMember(Member member) {
//        Authentication authentication = tokenProvider.getAuthenticationFromMember(member);
//        return tokenProvider.generateTokenDto(authentication);
//    }
//
//    // Register or find an OAuth2 user
//    public Member findOrRegisterOAuth2User(String provider, String providerId, String email, String name) {
//        return memberRepository.findByProviderAndProviderId(provider, providerId)
//                .orElseGet(() -> {
//                    Member newMember = Member.builder()
//                            .email(email)
//                            .name(name)
//                            .provider(provider)
//                            .providerId(providerId)
//                            .role(Member.Role.USER)
//                            .build();
//                    return memberRepository.save(newMember);
//                });
//    }
//}
