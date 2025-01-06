package com.capstone.project.member.entity;


import javax.persistence.*;
import lombok.*;

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
    private String currentCompany;

    @Column(name = "show_company", nullable = false)
    @Builder.Default
    private Boolean showCompany = true;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime registeredAt = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum Role {
        USER, ADMIN
    }
}
