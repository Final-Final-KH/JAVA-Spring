package com.capstone.project.myPage.dto;


import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberProfileResponseDto {
    private Integer memberId;
    private String email;
    private String name;
    private String phoneNumber;
    private String profilePictureUrl;
    private String currentCompany;
    private Boolean showCompany;

    private String location;
    private String bio;
    private String skills;
    private String resumeUrl;
}