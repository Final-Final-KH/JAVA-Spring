package com.capstone.project.member.dto.request;

import com.capstone.project.member.entity.Member;
import lombok.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 회원 요청 DTO
 * <p>
 * 회원 가입 및 정보 수정을 위한 요청 데이터를 담는 DTO 클래스입니다.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MemberRequestDto {
    private String email;
    private String password;
    private String name;
    private String phoneNumber;
    private String currentCompany;
    private Boolean showCompany;

    /**
     * 회원 엔티티로 변환하는 메서드
     *
     * @param passwordEncoder 비밀번호 인코더
     * @return Member 엔티티
     */
    public Member toEntity(PasswordEncoder passwordEncoder) {
        return Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .name(name)
                .phoneNumber(phoneNumber)
                .currentCompany(currentCompany == null || currentCompany.isEmpty() ? null : currentCompany)
                .showCompany(showCompany)
                .role(Member.Role.USER)
                .build();
    }

    /**
     * 인증용 토큰 생성 메서드
     *
     * @return UsernamePasswordAuthenticationToken
     */
    public UsernamePasswordAuthenticationToken toAuthentication() {
        return new UsernamePasswordAuthenticationToken(email, password);
    }
}
