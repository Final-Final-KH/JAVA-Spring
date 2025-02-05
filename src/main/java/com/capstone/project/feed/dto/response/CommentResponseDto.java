package com.capstone.project.feed.dto.response;

import com.capstone.project.feed.entity.FeedComment;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 댓글 응답 데이터 전송 객체
 *
 * 이 DTO는 댓글 및 대댓글 데이터를 클라이언트로 전송할 때 사용된다.
 * 추가로 작성자 이름(memberName)을 포함하여 프론트엔드에서 올바른 이름을 표시할 수 있도록 한다.
 */
@Getter
@Setter
@Builder(toBuilder = true) // toBuilder() 메서드 활성화
public class CommentResponseDto {
    private Integer commentId;             // 댓글 ID
    private Integer feedId;                // 피드 ID
    private Integer memberId;              // 작성자 ID
    private String memberName;             // 작성자 이름 (추가된 필드)
    private String comment;                // 댓글 내용
    private LocalDateTime createdAt;       // 생성 시간
    private LocalDateTime updatedAt;       // 수정 시간
    private String profilePictureUrl;      // 작성자의 프로필 사진 URL
    private List<CommentResponseDto> replies; // 대댓글 리스트
    private Long likesCount;               // 댓글 좋아요 수

    /**
     * Builder 패턴을 위한 생성자
     *
     * @param commentId         댓글 ID
     * @param feedId            피드 ID
     * @param memberId          작성자 ID
     * @param memberName        작성자 이름
     * @param comment           댓글 내용
     * @param createdAt         생성 시간
     * @param updatedAt         수정 시간
     * @param profilePictureUrl 작성자 프로필 사진 URL
     * @param replies           대댓글 리스트
     * @param likesCount        댓글 좋아요 수
     */
    @Builder
    public CommentResponseDto(Integer commentId, Integer feedId, Integer memberId, String memberName, String comment,
                              LocalDateTime createdAt, LocalDateTime updatedAt, String profilePictureUrl,
                              List<CommentResponseDto> replies, Long likesCount) {
        this.commentId = commentId;
        this.feedId = feedId;
        this.memberId = memberId;
        this.memberName = memberName;
        this.comment = comment;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.profilePictureUrl = profilePictureUrl;
        this.replies = replies;
        this.likesCount = likesCount;
    }

    /**
     * FeedComment 엔티티에서 DTO로 변환 (대댓글 제외)
     *
     * @param comment           FeedComment 엔티티
     * @param profilePictureUrl 작성자 프로필 사진 URL
     * @param memberName        작성자 이름
     * @param likesCount        댓글 좋아요 수
     */
    public CommentResponseDto(FeedComment comment, String profilePictureUrl, String memberName, Long likesCount) {
        this.commentId = comment.getCommentId();
        this.feedId = comment.getFeed().getFeedId();
        this.memberId = comment.getMemberId();
        this.memberName = memberName;
        this.comment = comment.getComment();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
        this.profilePictureUrl = profilePictureUrl;
        this.replies = List.of(); // 기본적으로 빈 리스트
        this.likesCount = likesCount;
    }

    /**
     * FeedComment 엔티티에서 DTO로 변환 (대댓글 포함)
     *
     * @param comment           FeedComment 엔티티
     * @param profilePictureUrl 작성자 프로필 사진 URL
     * @param includeReplies    대댓글 포함 여부 (true이면 대댓글도 매핑)
     * @param memberName        작성자 이름
     * @param likesCount        댓글 좋아요 수 (Service에서 전달받음)
     */
    public CommentResponseDto(FeedComment comment, String profilePictureUrl, boolean includeReplies, String memberName, Long likesCount) {
        // 올바른 생성자 호출: memberName(작성자 이름)과 likesCount를 순서대로 전달한다.
        this(comment, profilePictureUrl, memberName, likesCount);
        // 대댓글 처리 로직: includeReplies가 true이면 대댓글 목록을 매핑, 아니면 빈 리스트 반환
        this.replies = includeReplies && comment.getReplies() != null
                ? comment.getReplies().stream()
                .map(reply -> new CommentResponseDto(
                        reply,
                        profilePictureUrl, // 여기서는 동일한 프로필 사진과 작성자 이름을 사용
                        true,
                        memberName,
                        likesCount // 좋아요 수는 예시로 Service에서 전달받은 값을 사용; 실제로는 각 대댓글의 좋아요 수를 별도로 계산해야 할 수 있음
                ))
                .collect(Collectors.toList())
                : List.of();
    }
}
