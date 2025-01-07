package com.capstone.project.member.dto.response;

import com.capstone.project.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponseDto {
    private Integer memberId;
    private String email;
    private String name;
    private String phoneNumber;
    private String currentCompany;
    private Boolean showCompany;

    public MemberResponseDto(Member member) {
        this.memberId = member.getId();
        this.email = member.getEmail();
        this.name = member.getName();
        this.phoneNumber = member.getPhoneNumber();
        this.currentCompany = member.getShowCompany() ? member.getCurrentCompany() : "익명의 회사";
        this.showCompany = member.getShowCompany();
    }
}