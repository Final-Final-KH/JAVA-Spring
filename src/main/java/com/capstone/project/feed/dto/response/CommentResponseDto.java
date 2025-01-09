package com.capstone.project.feed.dto.response;

import com.capstone.project.feed.entity.FeedComment;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// 댓글 응답 데이터 전송 객체
@Getter
@Setter
@Builder
public class CommentResponseDto {
    private Integer commentId;      // 댓글 ID
    private Integer feedId;         // 피드 ID
    private Integer memberId;       // 작성자 ID
    private String comment;         // 댓글 내용
    private LocalDateTime createdAt; // 생성 시간
    private LocalDateTime updatedAt; // 수정 시간

    // FeedComment 엔티티에서 응답 DTO로 변환
    public CommentResponseDto(FeedComment comment) {
        this.commentId = comment.getCommentId();
        this.feedId = comment.getFeed().getFeedId();
        this.memberId = comment.getMemberId();
        this.comment = comment.getComment();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
    }

    // 기본 생성자 추가
    public CommentResponseDto(Integer commentId, Integer feedId, Integer memberId, String comment,
                              LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.commentId = commentId;
        this.feedId = feedId;
        this.memberId = memberId;
        this.comment = comment;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
