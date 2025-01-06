package com.capstone.project.member.dto;

import com.capstone.project.member.entity.Member3;
import com.capstone.project.member.entity.Member3.SubscriptionLevel;
import com.capstone.project.member.entity.Member3.Role;
import lombok.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;



@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberReqDto {

    private String email;

    private String password;

    private String name;

    private String phoneNumber;

    private String imagePath;

    @Builder.Default
    private SubscriptionLevel subscriptionLevel = SubscriptionLevel.FREE;

    @Builder.Default
    private Role role = Role.USER;

    private String currentCompany;

    @Builder.Default
    private Boolean showCompany = true;

    private String provider;

    private String providerId;

    public Member3 toEntity(PasswordEncoder passwordEncoder) {
        return Member3.builder()
                .email(this.email)
                .password(this.password)
                .name(this.name)
                .phoneNumber(this.phoneNumber)
                .imagePath(this.imagePath)
                .subscriptionLevel(this.subscriptionLevel != null ? this.subscriptionLevel : SubscriptionLevel.FREE)
                .role(this.role != null ? this.role : Role.USER)
                .currentCompany(this.currentCompany)
                .showCompany(this.showCompany != null ? this.showCompany : true)
                .provider(this.provider)
                .providerId(this.providerId)
                .build();
    }
    public UsernamePasswordAuthenticationToken toAuthentication() {
        return new UsernamePasswordAuthenticationToken(email, password);
    }
}
