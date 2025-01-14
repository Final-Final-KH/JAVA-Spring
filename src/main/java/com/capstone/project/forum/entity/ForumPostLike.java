package com.capstone.project.forum.entity;

import com.capstone.project.member.entity.Member;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 포럼 좋아요 Entity 클래스
 * 게시글 및 댓글의 좋아요 정보를 저장
 */
@Entity
@Table(name = "forum_post_likes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForumPostLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "forum_post_like_id")
    private Integer id; // 좋아요 고유 ID

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 좋아요를 누른 회원

    @ManyToOne
    @JoinColumn(name = "forum_post_id")
    private ForumPost forumPost; // 좋아요를 받은 게시글

    @ManyToOne
    @JoinColumn(name = "forum_post_comment_id")
    private ForumPostComment forumPostComment; // 좋아요를 받은 댓글

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now(); // 생성 시간

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
