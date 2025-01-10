package com.capstone.project.feed.dto.response;

import com.capstone.project.feed.entity.FeedComment;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// 댓글 응답 데이터 전송 객체
@Getter
@Setter
@Builder(toBuilder = true) // Enables the toBuilder() method

public class CommentResponseDto {
    private Integer commentId;             // 댓글 ID
    private Integer feedId;                // 피드 ID
    private Integer memberId;              // 작성자 ID
    private String comment;                // 댓글 내용
    private LocalDateTime createdAt;       // 생성 시간
    private LocalDateTime updatedAt;       // 수정 시간
    private String profilePictureUrl;      // 작성자의 프로필 사진 URL
    private List<CommentResponseDto> replies; // 대댓글 리스트
    private Long likesCount;               // 댓글 좋아요 수

    // 생성자 (Builder 패턴으로 사용하기 위한 생성자)
    @Builder
    public CommentResponseDto(Integer commentId, Integer feedId, Integer memberId, String comment,
                              LocalDateTime createdAt, LocalDateTime updatedAt, String profilePictureUrl,
                              List<CommentResponseDto> replies, Long likesCount) {
        this.commentId = commentId;               // 댓글 ID 초기화
        this.feedId = feedId;                     // 피드 ID 초기화
        this.memberId = memberId;                 // 작성자 ID 초기화
        this.comment = comment;                   // 댓글 내용 초기화
        this.createdAt = createdAt;               // 생성 시간 초기화
        this.updatedAt = updatedAt;               // 수정 시간 초기화
        this.profilePictureUrl = profilePictureUrl; // 작성자의 프로필 사진 URL 초기화
        this.replies = replies;                   // 대댓글 리스트 초기화
        this.likesCount = likesCount;             // 댓글 좋아요 수 초기화
    }

    // FeedComment 엔티티에서 DTO로 변환 (대댓글 제외)
    public CommentResponseDto(FeedComment comment, String profilePictureUrl, Long likesCount) {
        this.commentId = comment.getCommentId();         // 댓글 ID 설정
        this.feedId = comment.getFeed().getFeedId();     // 피드 ID 설정
        this.memberId = comment.getMemberId();           // 작성자 ID 설정
        this.comment = comment.getComment();             // 댓글 내용 설정
        this.createdAt = comment.getCreatedAt();         // 생성 시간 설정
        this.updatedAt = comment.getUpdatedAt();         // 수정 시간 설정
        this.profilePictureUrl = profilePictureUrl;      // 작성자의 프로필 사진 URL 설정
        this.replies = List.of();                        // 기본적으로 빈 리스트로 초기화
        this.likesCount = likesCount;                    // 댓글 좋아요 수 설정
    }

    // FeedComment 엔티티에서 DTO로 변환 (대댓글 포함)
    public CommentResponseDto(FeedComment comment, String profilePictureUrl, boolean includeReplies, Long likesCount) {
        this(comment, profilePictureUrl, likesCount); // 기본 생성자를 호출하여 초기화

        // 대댓글 처리 로직
        this.replies = includeReplies && comment.getReplies() != null
                ? comment.getReplies().stream()
                .map(reply -> new CommentResponseDto(
                        reply,
                        profilePictureUrl,
                        includeReplies,
                        likesCount // 좋아요 개수는 Service에서 전달받음
                ))
                .collect(Collectors.toList()) // 리스트로 변환
                : List.of(); // 대댓글이 없거나 includeReplies가 false인 경우 빈 리스트 반환
    }
}
