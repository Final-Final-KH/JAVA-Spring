package com.capstone.project.jwt.entity;

import com.capstone.project.member.entity.Member;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 해당 리프레시 토큰의 소유자

    @Column(nullable = false)
    private String refreshToken; // 리프레시 토큰

    @Column(nullable = false)
    private LocalDateTime expirationDate; // 리프레시 토큰 만료 시간

    // 리프레시 토큰 유효성 검사를 위한 메소드
    public boolean isExpired() {
        return expirationDate.isBefore(LocalDateTime.now());
    }
}