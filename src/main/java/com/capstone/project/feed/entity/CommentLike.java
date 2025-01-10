package com.capstone.project.feed.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

// 댓글 좋아요 엔티티
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "comment_likes", uniqueConstraints = @UniqueConstraint(columnNames = {"comment_id", "member_id"}))
public class CommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer likeId; // 좋아요 ID

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false)
    private FeedComment comment; // 연결된 댓글

    @Column(name = "member_id", nullable = false)
    private Integer memberId; // 좋아요를 누른 회원 ID

    @Column(nullable = false, updatable = false)
    @org.hibernate.annotations.CreationTimestamp
    private LocalDateTime createdAt; // 좋아요 생성 시간
}
