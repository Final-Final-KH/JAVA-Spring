package com.capstone.project.member.dto.request;

import com.capstone.project.member.entity.Member;
import lombok.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    public Member toEntity(PasswordEncoder passwordEncoder) {
        return Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .name(name)
                .phoneNumber(phoneNumber)
                .currentCompany(currentCompany)
                .showCompany(showCompany)
                .role(Member.Role.USER)
                .build();
    }
    public UsernamePasswordAuthenticationToken toAuthentication() {
        return new UsernamePasswordAuthenticationToken(email, password);
    }
}
