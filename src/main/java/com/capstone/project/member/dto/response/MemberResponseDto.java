package com.capstone.project.member.dto.response;

import com.capstone.project.member.entity.Member;
import lombok.*;

/**
 * 회원 응답 DTO
 * <p>
 * 클라이언트에 반환할 회원 정보를 담는 DTO 클래스입니다.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberResponseDto {
    private Integer memberId;           // 회원 ID
    private String email;               // 이메일
    private String name;                // 이름
    private String phoneNumber;         // 전화번호
    private String currentCompany;      // 현재 회사명
    private Boolean showCompany;        // 회사명 표시 여부
    private String profilePictureUrl;   // 프로필 사진 URL (새로운 필드 추가)
    private String role;

    /**
     * Member 엔티티를 DTO로 변환하는 생성자
     *
     * @param member Member 엔티티
     */
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

    @Override
    public String toString() {
        return "MemberResponseDto{" +
                "memberId=" + memberId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
