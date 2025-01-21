package com.capstone.project.forum.entity;

import com.capstone.project.member.entity.Member;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "forum_post_likes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"member_id", "postId"}), // 게시글 좋아요 중복 방지
        @UniqueConstraint(columnNames = {"member_id", "commentId"}) // 댓글 좋아요 중복 방지
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForumPostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // 고유 ID

    private Integer postId; // 게시글 ID (댓글 좋아요가 아닌 경우에만 사용)
    private Integer commentId; // 댓글 ID (게시글 좋아요가 아닌 경우에만 사용)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 좋아요를 누른 사용자

    private LocalDateTime createdAt = LocalDateTime.now(); // 좋아요 생성 시간

    /**
     * 게시글 좋아요를 위한 생성자
     *
     * @param postId 게시글 ID
     * @param member 좋아요를 누른 사용자 객체
     */
    public ForumPostLike(Integer postId, Member member) {
        this.postId = postId;
        this.commentId = null; // 게시글 좋아요이므로 댓글 ID는 null
        this.member = member;
    }

    /**
     * 댓글 좋아요를 위한 생성자
     *
     * @param commentId 댓글 ID
     * @param member 좋아요를 누른 사용자 객체
     */
    public ForumPostLike(Integer commentId, Member member, boolean isCommentLike) {
        this.commentId = commentId;
        this.postId = null; // 댓글 좋아요이므로 게시글 ID는 null
        this.member = member;
    }
}
