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
public class Feed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer feedId; // Feed ID / 게시물 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // Member who posted / 게시물을 작성한 사용자

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // Content of the feed / 게시물 내용

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // Created timestamp / 생성 시간

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now(); // Updated timestamp / 수정 시간

    @Column(nullable = false)
    private Integer likesCount = 0; // Number of likes / 좋아요 개수

    // Constructor for initializing Feed with only feedId / feedId만 초기화하는 생성자
    public Feed(Integer feedId) {
        this.feedId = feedId; // Initialize the feedId / feedId를 초기화
    }
}
