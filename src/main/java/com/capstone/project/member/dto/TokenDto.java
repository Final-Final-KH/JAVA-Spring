package com.capstone.project.member.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenDto {
    private String grantType;     // 인증 방식
    private String accessToken;   // 액세스 토큰
    private Long accessTokenExpiresIn;  // 엑세스 토큰 만료 시간
    private String refreshToken;  // 리플레시 토큰
    private Long refreshTokenExpiresIn;  // 리플레시 토큰 만료 시간
}