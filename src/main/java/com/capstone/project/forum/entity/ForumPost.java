package com.capstone.project.forum.entity;

import com.capstone.project.member.entity.Member;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 포럼 게시글 Entity 클래스
 * 게시글 관련 데이터를 저장
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
    private Integer id; // 게시글 고유 ID

    @ManyToOne
    @JoinColumn(name = "forum_category_id", nullable = false)
    private ForumCategory forumCategory; // 해당 게시글의 카테고리

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 게시글 작성자

    @Column(nullable = false, length = 255)
    private String title; // 게시글 제목

    @Column(columnDefinition = "TEXT")
    private String content; // 게시글 내용

    @Column(nullable = false)
    private Boolean sticky = false; // 상단 고정 여부

    @Column(nullable = false)
    private Integer viewsCount = 0; // 조회수

    @Column(nullable = false)
    private Integer likesCount = 0; // 좋아요 수

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 생성 시간

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now(); // 수정 시간

    @OneToMany(mappedBy = "forumPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ForumPostComment> comments = new ArrayList<>(); // 댓글 리스트

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
