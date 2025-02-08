package com.capstone.project.member.entity;

import javax.persistence.*;
import com.capstone.project.jwt.entity.RefreshToken;
import com.capstone.project.kedu.entity.board.*;
import com.capstone.project.kedu.entity.comment.*;
import com.capstone.project.kedu.entity.edu.MyAcademyEntity2;
import com.capstone.project.kedu.entity.edu.MyCourseEntity2;
import com.capstone.project.kedu.entity.mypage.SkillHubEntity2;
import com.capstone.project.kedu.entity.survey.SurveyEntity2;
import com.capstone.project.myPage.entity.Profile;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 회원(Entity) 클래스
 * <p>
 * 회원 정보를 데이터베이스에 저장 및 관리하는 엔티티 클래스입니다.
 * </p>
 */
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
    private Integer id; // 회원 ID

    @Column(nullable = true, unique = true, length = 100)
    private String email; // 이메일 (로그인 ID)

    @Column(nullable = true)
    private String password; // 비밀번호 (OAuth 사용자는 NULL 가능)

    @Column(nullable = false, length = 50)
    private String name; // 사용자 이름

    @Column(name = "phone_number", unique = true, length = 15)
    private String phoneNumber; // 전화번호

    @Column(name = "provider", length = 50)
    private String provider; // OAuth 제공자 (Google, Kakao 등)

    @Column(name = "provider_id", length = 255)
    private String providerId; // OAuth 제공자로부터 받은 고유 ID

    /**
     * 사용자 역할 (USER, ADMIN)
     * 기본값: USER
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER;

    @Column(name = "current_company", length = 255)
    @Builder.Default
    private String currentCompany = "Unemployed"; // 현재 소속 회사 (기본값: Unemployed)

    @Column(name = "show_company")
    @Builder.Default
    private Boolean showCompany = false; // 회사 공개 여부 (기본값: false)

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime registeredAt = LocalDateTime.now(); // 등록 시간

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now(); // 마지막 수정 시간

    @Column(name = "profile_picture_url", length = 255)
    private String profilePictureUrl; // 프로필 사진 URL

    // 연관 엔티티 매핑

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<KeduBoardEntity2> boards = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SurveyEntity2> surveys = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MyCourseEntity2> myCourses = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<KeduBoardCommentEntity2> boardComments = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<KeduBoardCommentsCommentsEntity2> boardReplies = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AcademyCommentEntity2> academyComments = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseCommentEntity2> courseComments = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SkillHubEntity2> skillHubEntities = new ArrayList<>();

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Profile profile;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MyAcademyEntity2> myAcademy = new ArrayList<>();

    /**
     * 리프레시 토큰 매핑
     * <p>
     * 회원과 연관된 리프레시 토큰을 저장합니다.
     * 회원이 삭제되면 해당 리프레시 토큰들도 함께 삭제됩니다.
     * </p>
     */
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefreshToken> refreshTokens = new ArrayList<>();

    /**
     * 친구 목록 (다대다 관계)
     * <p>
     * 한 회원이 여러 친구를 가질 수 있으며, 친구 목록은 회원이 삭제되어도 유지됩니다.
     * </p>
     */
    @ManyToMany
    @JoinTable(
            name = "member_friends",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private List<Member> friends = new ArrayList<>();

    /**
     * 사용자 역할 Enum
     * <p>
     * USER: 일반 사용자<br>
     * ADMIN: 관리자
     * </p>
     */
    public enum Role {
        USER, ADMIN
    }

    /**
     * 사용자 ID 반환 메서드
     *
     * @return Integer 사용자 ID
     */
    public Integer getMemberId() {
        return this.id;
    }

    /**
     * 사용자 생성자 (빌더 패턴)
     * <p>
     * friends 필드를 빈 리스트로 초기화하여 NullPointerException을 방지합니다.
     * </p>
     *
     * @param email              이메일
     * @param password           비밀번호
     * @param name               사용자 이름
     * @param phoneNumber        전화번호
     * @param role               사용자 역할
     * @param currentCompany     현재 소속 회사
     * @param showCompany        회사 공개 여부
     * @param provider           OAuth 제공자
     * @param providerId         OAuth 제공자로부터 받은 고유 ID
     * @param profilePictureUrl  프로필 사진 URL
     */
    @Builder
    public Member(
            String email,
            String password,
            String name,
            String phoneNumber,
            Role role,
            String currentCompany,
            Boolean showCompany,
            String provider,
            String providerId,
            String profilePictureUrl
    ) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.role = role != null ? role : Role.USER;
        this.currentCompany = currentCompany != null ? currentCompany : "Unemployed";
        this.showCompany = showCompany != null ? showCompany : false;
        this.provider = provider;
        this.providerId = providerId;
        this.profilePictureUrl = profilePictureUrl;
        this.registeredAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.friends = new ArrayList<>();
    }

    /**
     * ID만 초기화하는 생성자
     *
     * @param memberId 사용자 ID
     */
    public Member(Integer memberId) {
        this.id = memberId;
    }

    /**
     * 등록 전 이벤트 핸들러
     * 등록 시간과 수정 시간을 현재 시간으로 설정합니다.
     */
    @PrePersist
    protected void onCreate() {
        this.registeredAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 수정 전 이벤트 핸들러
     * 수정 시간을 현재 시간으로 갱신합니다.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
