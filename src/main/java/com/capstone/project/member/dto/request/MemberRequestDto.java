package com.capstone.project.member.dto.request;

import lombok.*;

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
}
