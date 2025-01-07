package com.capstone.project.member.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Integer id; // Primary key for members / 회원 고유 식별자

    @Column(nullable = false, unique = true, length = 100)
    private String email; // Member's email address / 회원 이메일 주소

    @Column(nullable = true)
    private String password; // Password for standard users (nullable for OAuth users) / 일반 사용자 비밀번호 (OAuth 사용자는 null 가능)

    @Column(nullable = false, length = 50)
    private String name; // Member's name / 회원 이름

    @Column(name = "phone_number", unique = true, length = 15)
    private String phoneNumber; // Member's phone number / 회원 전화번호

    @Column(name = "provider", length = 50)
    private String provider; // OAuth provider (e.g., Google, Kakao) / OAuth 제공자 (Google, Kakao 등)

    @Column(name = "provider_id", length = 255)
    private String providerId; // Unique provider ID from OAuth / OAuth 제공자의 고유 ID

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER; // Role of the member (default: USER) / 회원 권한 (기본값: USER)

    @Column(name = "current_company", length = 255)
    private String currentCompany; // Current company name / 현재 근무 중인 회사 이름

    @Column(name = "show_company", nullable = false)
    @Builder.Default
    private Boolean showCompany = true; // Whether to display the company / 회사 공개 여부

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime registeredAt = LocalDateTime.now(); // Registration timestamp / 등록 시간

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now(); // Last updated timestamp / 마지막 수정 시간

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now(); // Automatically update `updatedAt` before saving / 저장 전 `updatedAt` 자동 업데이트
    }

    public Member(Integer memberId) {
        this.id = memberId; // Only initialize the ID / ID만 초기화
    }


    // Add a custom getter for `id` with the name `getMemberId` for compatibility with FeedService / FeedService와 호환성을 위한 `getMemberId` 추가
    public Integer getMemberId() {
        return this.id;
    }

    public enum Role {
        USER, ADMIN // User roles / 사용자 역할
    }


}
