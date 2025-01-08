package com.capstone.project.feed.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

// 피드 응답 데이터 전송 객체
@Getter
@Setter
@Builder
public class FeedResponseDto {
    private Integer feedId;             // 피드 ID
    private Integer memberId;           // 작성자 ID
    private String content;             // 피드 내용
    private LocalDateTime createdAt;    // 피드 생성 시간
    private LocalDateTime updatedAt;    // 피드 마지막 수정 시간
    private Integer likesCount;         // 좋아요 수
    private Integer repostedFrom;       // 리포스트된 피드 ID (nullable)
    private String repostedFromContent; // 리포스트된 피드 내용 (nullable)
    private List<CommentResponseDto> comments; // 해당 피드의 댓글 리스트

    // 기본 생성자 (엔티티 변환 시 사용 가능)
    public FeedResponseDto(Integer feedId, Integer memberId, String content, LocalDateTime createdAt,
                           LocalDateTime updatedAt, Integer likesCount, Integer repostedFrom,
                           String repostedFromContent, List<CommentResponseDto> comments) {
        this.feedId = feedId;
        this.memberId = memberId;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.likesCount = likesCount;
        this.repostedFrom = repostedFrom;
        this.repostedFromContent = repostedFromContent;
        this.comments = comments;
    }
}
