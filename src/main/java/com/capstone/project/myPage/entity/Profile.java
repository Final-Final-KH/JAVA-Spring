package com.capstone.project.myPage.entity;


import com.capstone.project.member.entity.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 프로필 Entity 클래스
 * 회원의 추가적인 프로필 정보를 관리
 */
@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Profile {

    @Id
    @Column(name = "member_id") // PK 및 FK로 설정
    private Integer memberId;

    @OneToOne
    @MapsId // Member 엔티티와의 1:1 관계
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(columnDefinition = "TEXT")
    private String bio; // 자기소개

    @Column
    private String location;

    @Column
    private String skills;

    @Column(name = "resume_url", length = 2083)
    private String resumeUrl; // 이력서 URL

}