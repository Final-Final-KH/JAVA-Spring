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
@Table(name = "saved_posts")
public class SavedPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer savedPostId; // 저장된 게시물 고유 ID

    @Column(nullable = false)
    private Integer memberId; // 저장한 사용자 ID

    @ManyToOne
    @JoinColumn(name = "feed_id", foreignKey = @ForeignKey(name = "FK_saved_post_feed_id"))
    private Feed feed; // 저장된 피드

    private LocalDateTime savedAt; // 저장된 시간
}
