package com.capstone.project.jwt.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenDto {

    private String refreshToken; // 리프레시 토큰
    private Long refreshTokenExpiresIn; // 리프레시 토큰 만료 시간 (밀리초)
}
