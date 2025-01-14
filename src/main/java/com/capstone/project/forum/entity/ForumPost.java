package com.capstone.project.forum.entity;

import com.capstone.project.member.entity.Member;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 포럼 게시글 Entity 클래스
 * 게시글 데이터를 관리
 */
@Entity
@Table(name = "forum_posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForumPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "forum_post_id")
    private Integer id; // 게시글 ID

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private ForumCategory forumCategory; // 게시글 카테고리

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 작성자

    @OneToMany(mappedBy = "forumPost", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ForumPostComment> comments = new ArrayList<>(); // 게시글의 댓글 리스트

    @Column(nullable = false)
    private String title; // 게시글 제목

    @Column(columnDefinition = "TEXT")
    private String content; // 게시글 내용

    @Column(nullable = false)
    @Builder.Default
    private Boolean sticky = false; // 상단 고정 여부

    @Column(nullable = false)
    @Builder.Default
    private Integer viewsCount = 0; // 조회수

    @Column(nullable = false)
    @Builder.Default
    private Integer likesCount = 0; // 좋아요 수

    @Column(nullable = false)
    @Builder.Default
    private Boolean hidden = false; // 숨김 여부 (신고 누적 시 설정)

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now(); // 생성 시간

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now(); // 수정 시간

    @Column(name = "removed_by")
    private String removedBy; // 삭제자 정보 ("OP", "ADMIN" 등)

    @ElementCollection
    @CollectionTable(name = "forum_post_files", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "file_url")
    @Builder.Default
    private List<String> fileUrls = new ArrayList<>(); // 첨부 파일 URL 목록

    /**
     * 파일 URL 추가 메서드
     *
     * @param fileUrl 추가할 파일 URL
     */
    public void addFileUrl(String fileUrl) {
        this.fileUrls.add(fileUrl); // 파일 URL 추가
    }

    /**
     * 엔티티 생성 이벤트 처리 메서드
     * 생성 시간과 수정 시간을 현재 시간으로 설정
     */
    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 엔티티 업데이트 이벤트 처리 메서드
     * 수정 시간을 현재 시간으로 설정
     */
    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 작성자 이름 반환 메서드
     *
     * @return 작성자 이름
     */
    public String getAuthorName() {
        return this.member != null ? this.member.getName() : "Unknown"; // 작성자 이름 반환, 없으면 Unknown
    }
}
