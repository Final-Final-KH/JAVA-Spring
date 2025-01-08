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
public class CommentResponseDto {
    private Integer commentId;      // 댓글 ID
    private Integer feedId;         // 피드 ID
    private Integer memberId;       // 작성자 ID
    private String comment;         // 댓글 내용
    private LocalDateTime createdAt; // 생성 시간
    private LocalDateTime updatedAt; // 수정 시간
    private List<CommentResponseDto> replies; // 대댓글 리스트

    // 생성자 (Builder 패턴으로 사용하기 위한 생성자)
    @Builder
    public CommentResponseDto(Integer commentId, Integer feedId, Integer memberId, String comment,
                              LocalDateTime createdAt, LocalDateTime updatedAt, List<CommentResponseDto> replies) {
        this.commentId = commentId; // 댓글 ID 초기화
        this.feedId = feedId; // 피드 ID 초기화
        this.memberId = memberId; // 작성자 ID 초기화
        this.comment = comment; // 댓글 내용 초기화
        this.createdAt = createdAt; // 생성 시간 초기화
        this.updatedAt = updatedAt; // 수정 시간 초기화
        this.replies = replies; // 대댓글 리스트 초기화
    }

    // FeedComment 엔티티에서 DTO로 변환 (대댓글 제외)
    // API 개선 사항:
    // 1. API 사용성 향상: 빈 경우에도 배열(List)을 반환하면 클라이언트가 더 쉽게 사용할 수 있다.
    // 2. 엣지 케이스 감소: 클라이언트 측에서 null 체크가 불필요해진다.
    // 3. 미래 대비: 대댓글 처리가 더 복잡해지는 경우에도, 일관된 접근 방식으로 잠재적인 버그를 줄일 수 있다.
    public CommentResponseDto(FeedComment comment) {
        this.commentId = comment.getCommentId();
        this.feedId = comment.getFeed().getFeedId();
        this.memberId = comment.getMemberId();
        this.comment = comment.getComment();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
        this.replies = List.of(); // 기본적으로 빈 리스트로 설정
    }

    // FeedComment 엔티티에서 DTO로 변환 (대댓글 포함)
    // 대댓글 처리 로직을 포함한 생성자
    public CommentResponseDto(FeedComment comment, boolean includeReplies) {
        this(comment); // 기본 생성자를 호출하여 초기화

        // 대댓글 처리 로직:
        // includeReplies가 true인 경우에만 대댓글 리스트를 생성하고 변환.
        this.replies = includeReplies && comment.getReplies() != null
                ? comment.getReplies().stream()
                .map(reply -> new CommentResponseDto(reply, true)) // 재귀적으로 대댓글 변환
                .collect(Collectors.toList()) // 리스트로 변환
                : List.of(); // 대댓글이 없거나 includeReplies가 false인 경우 빈 리스트 반환
    }
}
