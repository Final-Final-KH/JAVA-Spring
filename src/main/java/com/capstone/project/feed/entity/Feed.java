package com.capstone.project.feed.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// 피드 엔티티
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "feeds")
public class Feed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer feedId; // 피드 고유 ID

    @Column(nullable = false)
    private Integer memberId; // 작성자 ID

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // 게시물 내용

    private LocalDateTime createdAt; // 생성 시간
    private LocalDateTime updatedAt; // 마지막 수정 시간

    private Integer likesCount; // 좋아요 수

    @ManyToOne
    @JoinColumn(name = "reposted_from", foreignKey = @ForeignKey(name = "FK_reposted_from"))
    private Feed repostedFrom; // 원본 피드

    @Column(name = "reposter_id", nullable = true)
    private Integer reposterId; // 리포스트한 회원 ID

    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FeedComment> comments = new ArrayList<>(); // 연결된 댓글 리스트

    @Column(name = "reposted_from_content", columnDefinition = "TEXT", nullable = true)
    private String repostedFromContent; // 리포스트된 원본 피드 내용 (스냅샷)

    @Transient
    public boolean isRepost() {
        return repostedFrom != null;
    } // 리포스트 여부 계산
}
