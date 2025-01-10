package com.capstone.project.feed.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

// 피드 좋아요 엔티티
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "feed_likes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"feed_id", "member_id"}) // 중복 좋아요 방지
})
public class FeedLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer likeId; // 좋아요 고유 ID

    @ManyToOne
    @JoinColumn(name = "feed_id", foreignKey = @ForeignKey(name = "FK_feed_like_feed_id"), nullable = false)
    private Feed feed; // 좋아요가 속한 피드

    @Column(nullable = false, name = "member_id")
    private Integer memberId; // 좋아요를 누른 회원 ID

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성 시간

    // 생성 시간 설정 로직
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
