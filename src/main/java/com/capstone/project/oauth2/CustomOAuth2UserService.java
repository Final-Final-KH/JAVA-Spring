// 추후 사용

//package com.capstone.project.oauth2;

//
//import com.capstone.project.jwt.TokenProvider;
//import com.capstone.project.member.entity.Member;
//import com.capstone.project.member.repository.MemberRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
//import org.springframework.stereotype.Service;
//
//import java.util.Collections;
//import java.util.Map;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class CustomOAuth2UserService extends DefaultOAuth2UserService {
//
//    private final MemberRepository memberRepository;
//    private final TokenProvider tokenProvider;
//
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
//        OAuth2User oAuth2User = super.loadUser(userRequest);
//
//        String provider = userRequest.getClientRegistration().getRegistrationId();
//        Map<String, Object> attributes = oAuth2User.getAttributes();
//        String providerId = attributes.get("sub").toString(); // Adjust based on the provider (e.g., Google/Kakao).
//        String email = attributes.get("email").toString();
//        String name = attributes.getOrDefault("name", "Anonymous").toString();
//
//        Member member = memberRepository.findByProviderAndProviderId(provider, providerId)
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
//
//        Authentication authentication = tokenProvider.getAuthenticationFromMember(member);
//
//        // Return a new OAuth2User with additional attributes
//        return new DefaultOAuth2User(
//                Collections.singleton(new OAuth2UserAuthority(attributes)),
//                attributes,
//                "email"
//        );
//    }
//}
