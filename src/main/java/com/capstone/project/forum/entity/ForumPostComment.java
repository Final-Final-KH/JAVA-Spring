package com.capstone.project.forum.entity;

import com.capstone.project.member.entity.Member;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 포럼 댓글 Entity 클래스
 * 각 댓글에 대한 데이터를 저장
 */
@Entity
@Table(name = "forum_post_comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForumPostComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "forum_post_comment_id")
    private Integer id; // 댓글 고유 ID

    @ManyToOne
    @JoinColumn(name = "forum_post_id", nullable = false)
    private ForumPost forumPost; // 댓글이 속한 게시글

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 댓글 작성자

    @Column(columnDefinition = "TEXT")
    private String content; // 댓글 내용

    @Column(nullable = false)
    private Integer likesCount = 0; // 댓글 좋아요 수

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 생성 시간

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now(); // 수정 시간

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
