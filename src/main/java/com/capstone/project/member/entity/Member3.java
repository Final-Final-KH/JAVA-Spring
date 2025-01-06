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
public class Member3 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "phone_number", unique = true, length = 15)
    private String phoneNumber;

    @Builder.Default
    @Column(name = "registered_at", nullable = false, updatable = false)
    private LocalDateTime registeredAt = LocalDateTime.now();

    @Column(name = "image_path")
    private String imagePath;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_level", nullable = false)
    private SubscriptionLevel subscriptionLevel = SubscriptionLevel.FREE;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(name = "current_company", length = 255)
    private String currentCompany; // The user's company name

    @Builder.Default
    @Column(name = "show_company", nullable = false)
    private Boolean showCompany = true; // Whether to display the company name or not

    @Builder.Default
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "provider", length = 50)
    private String provider;

    @Column(name = "provider_id", length = 255)
    private String providerId;

    @PrePersist
    protected void onCreate() {
        this.registeredAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum SubscriptionLevel {
        FREE, PAID
    }

    public enum Role {
        USER, ADMIN
    }
}