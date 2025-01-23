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
    private Integer memberId;           // 멤버 ID
    private String email;               // 이메일
    private String name;                // 이름
    private String phoneNumber;         // 전화번호
    private String currentCompany;      // 현재 회사명
    private Boolean showCompany;        // 회사명 표시 여부
    private String profilePictureUrl;   // 프로필 사진 URL (새로운 필드 추가)
    private String role;

    // 기본 생성자
    public MemberResponseDto(Member member) {
        this.memberId = member.getId();
        this.email = member.getEmail();
        this.name = member.getName();
        this.phoneNumber = member.getPhoneNumber();
        this.currentCompany = member.getShowCompany() ? member.getCurrentCompany() : "익명의 회사";
        this.showCompany = member.getShowCompany();
        this.profilePictureUrl = member.getProfilePictureUrl(); // 프로필 사진 URL 추가
        this.role = member.getRole().name();
    }
}
