package com.capstone.project.member.entity;


import javax.persistence.*;

import com.capstone.project.kedu.entity.board.KeduBoardEntity2;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder // Adds the builder pattern to your entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = true)
    private String password; // Password can be null for OAuth users

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "phone_number", unique = true, length = 15)
    private String phoneNumber;

    @Column(name = "provider", length = 50)
    private String provider; // Google or Kakao

    @Column(name = "provider_id", length = 255)
    private String providerId; // Unique ID from provider

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER;

    @Column(name = "current_company", length = 255)
    @Builder.Default
    private String currentCompany = "Unemployed";

    @Column(name = "show_company")
    @Builder.Default
    private Boolean showCompany = false;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime registeredAt = LocalDateTime.now(); // Registration timestamp / 등록 시간

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now(); // Last updated timestamp / 마지막 수정 시간



    @PrePersist
    protected void onCreate() {
        this.registeredAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<KeduBoardEntity2> boards = new ArrayList<>();

    @Builder
    public Member (String email, String password, String name, String phoneNumber, Role role, String currentCompany, boolean showCompany, String provider, String providerId) {
        this.email= email;
        this.password=password;
        this.name=name;
        this.phoneNumber=phoneNumber;
        this.role=role;
        this.currentCompany=currentCompany;
        this.showCompany=showCompany;
        this.provider=provider;
        this.providerId=providerId;
        this.registeredAt=LocalDateTime.now();
        this.updatedAt=LocalDateTime.now();

    }


    public Member(Integer memberId) {
        this.id = memberId; // Only initialize the ID / ID만 초기화
    }

    // Add a custom getter for `id` with the name `getMemberId` for compatibility with FeedService / FeedService와 호환성을 위한 `getMemberId` 추가
    public Integer getMemberId() {
        return this.id;
    }


    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum SubscriptionLevel {
        FREE, PAID
    }

    public enum Role {
        USER, ADMIN // User roles / 사용자 역할
    }
}