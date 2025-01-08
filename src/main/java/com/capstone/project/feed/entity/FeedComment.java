package com.capstone.project.feed.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// 피드 댓글 엔티티
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "feed_comments")
public class FeedComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer commentId; // 댓글 ID

    @ManyToOne
    @JoinColumn(name = "feed_id", foreignKey = @ForeignKey(name = "FK_feed_comment_feed_id"))
    private Feed feed; // 연결된 피드

    @Column(nullable = false)
    private Integer memberId; // 작성자 ID

    @Column(nullable = false, columnDefinition = "TEXT")
    private String comment; // 댓글 내용

    @ManyToOne
    @JoinColumn(name = "parent_comment_id", foreignKey = @ForeignKey(name = "FK_feed_comment_parent_comment"))
    private FeedComment parentComment; // 부모 댓글 (대댓글 기능)

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FeedComment> replies = new ArrayList<>(); // 대댓글 리스트


    @Column(nullable = false, updatable = false)
    @org.hibernate.annotations.CreationTimestamp
    private LocalDateTime createdAt; // 생성 시간

    @Column(nullable = false)
    @org.hibernate.annotations.UpdateTimestamp
    private LocalDateTime updatedAt; // 수정 시간
}
