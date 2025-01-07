package com.capstone.project.feed.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

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
    private Integer commentId; // 댓글 고유 ID

    @ManyToOne
    @JoinColumn(name = "feed_id", foreignKey = @ForeignKey(name = "FK_feed_id"))
    private Feed feed; // 연결된 피드

    @Column(nullable = false)
    private Integer memberId; // 작성자 ID

    @Column(nullable = false, columnDefinition = "TEXT")
    private String comment; // 댓글 내용

    private LocalDateTime createdAt; // 생성 시간
    private LocalDateTime updatedAt; // 마지막 수정 시간
}
