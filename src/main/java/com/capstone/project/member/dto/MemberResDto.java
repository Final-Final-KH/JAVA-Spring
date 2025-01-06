package com.capstone.project.member.dto;

import com.capstone.project.member.entity.Member3;
import lombok.*;

import com.capstone.project.member.entity.Member3.SubscriptionLevel;
import com.capstone.project.member.entity.Member3.Role;


import java.time.LocalDateTime;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
@Builder
public class MemberResDto {

    private Integer id;

    private String email;

    private String name;

    private String phoneNumber;

    private LocalDateTime registeredAt;

    private String imagePath;

    private SubscriptionLevel subscriptionLevel;

    private Role role;

    private String currentCompany;

    private Boolean showCompany;

    private LocalDateTime updatedAt;

    private String provider;

    private String providerId;

    public static MemberResDto of(Member3 member) {
        return MemberResDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .phoneNumber(member.getPhoneNumber())
                .registeredAt(member.getRegisteredAt())
                .imagePath(member.getImagePath())
                .subscriptionLevel(member.getSubscriptionLevel())
                .role(member.getRole())
                .currentCompany(member.getCurrentCompany())
                .showCompany(member.getShowCompany())
                .updatedAt(member.getUpdatedAt())
                .provider(member.getProvider())
                .providerId(member.getProviderId())
                .build();
    }
}
