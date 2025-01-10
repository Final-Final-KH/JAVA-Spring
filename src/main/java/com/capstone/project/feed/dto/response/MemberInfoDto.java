package com.capstone.project.feed.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

// 회원 정보 DTO
@Getter
@Setter
@Builder
public class MemberInfoDto {
    private Integer memberId; // 회원 ID
    private String name;      // 회원 이름
    private String profilePictureUrl; // 프로필 사진 URL
}
