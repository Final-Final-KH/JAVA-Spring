//package com.capstone.project.oauth2;
//
//import com.capstone.project.member.entity.Member;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Map;
//
//public class CustomOauth2UserDetails implements UserDetails, OAuth2User {
//
//    private final Member member;
//    private Map<String, Object> attributes;
//
//    public CustomOauth2UserDetails(Member member, Map<String, Object> attributes) {
//        this.member = member;
//        this.attributes = attributes;
//    }
//
//    @Override
//    public Map<String, Object> getAttributes() {
//        return attributes;
//    }
//
//    @Override
//    public String getName() {
//        return member.getName(); // 구글 OAuth2 사용자 이름을 사용
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        Collection<GrantedAuthority> authorities = new ArrayList<>();
//        authorities.add(() -> member.getRole().name()); // 사용자 역할
//        return authorities;
//    }
//
//    @Override
//    public String getPassword() {
//        return member.getPassword(); // 패스워드는 OAuth 사용자에게는 null일 수 있음
//    }
//
//    @Override
//    public String getUsername() {
//        return member.getEmail(); // 로그인 아이디로 이메일 사용
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
//
//    // 구글 로그인에서 반환된 userId를 기준으로 Member 정보를 저장하거나 업데이트
//    public static Member fromOAuth2User(Map<String, Object> attributes) {
//        String email = (String) attributes.get("email");
//        String name = (String) attributes.get("name");
//        String provider = "google"; // 구글 로그인
//        String providerId = (String) attributes.get("sub"); // Google OAuth2에서 'sub' 값은 고유 ID입니다.
//        String profilePictureUrl = (String) attributes.get("picture");
//
//        // 이미 존재하는 이메일로 사용자 찾기
//        Member member = new Member();
//        member.setEmail(email);
//        member.setName(name);
//        member.setProvider(provider);
//        member.setProviderId(providerId);
//        member.setProfilePictureUrl(profilePictureUrl);
//
//        // 패스워드가 필요 없으면 null로 설정
//        member.setPassword(null);
//
//        return member;
//    }
//}
