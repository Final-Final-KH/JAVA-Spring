package com.capstone.project.feed.entity;

import com.capstone.project.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class SavedPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer savedPostId; // Saved Post ID / 저장된 게시물 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // Member who saved the post / 게시물을 저장한 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id", nullable = false)
    private Feed feed; // Saved feed / 저장된 게시물

    @Column(nullable = false)
    private LocalDateTime savedAt = LocalDateTime.now(); // When the post was saved / 저장 시간
}
