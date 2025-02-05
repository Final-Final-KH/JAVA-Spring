package com.capstone.project.feed.dto.response;

import com.capstone.project.feed.entity.FeedComment;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 댓글 응답 데이터 전송 객체.
 * 이 DTO는 댓글 및 대댓글 데이터를 클라이언트로 전송할 때 사용되며,
 * 작성자 이름(memberName), 프로필 사진 URL, 댓글 내용, 생성/수정 시간,
 * 좋아요 수(likesCount)와 함께 현재 사용자가 해당 댓글을 좋아요 했는지를 나타내는 liked 필드를 포함한다.
 */
@Getter
@Setter
@Builder(toBuilder = true)
public class CommentResponseDto {
    private Integer commentId;             // 댓글 ID
    private Integer feedId;                // 피드 ID
    private Integer memberId;              // 작성자 ID
    private String memberName;             // 작성자 이름
    private String comment;                // 댓글 내용
    private LocalDateTime createdAt;       // 생성 시간
    private LocalDateTime updatedAt;       // 수정 시간
    private String profilePictureUrl;      // 작성자의 프로필 사진 URL
    private List<CommentResponseDto> replies; // 대댓글 리스트
    private Long likesCount;               // 댓글 좋아요 수
    private boolean liked;                 // 현재 사용자가 이 댓글을 좋아요 했는지 여부

    /**
     * 모든 필드를 포함하는 생성자.
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
     * @param liked             현재 사용자가 좋아요 했는지 여부
     */
    @Builder
    public CommentResponseDto(Integer commentId, Integer feedId, Integer memberId, String memberName, String comment,
                              LocalDateTime createdAt, LocalDateTime updatedAt, String profilePictureUrl,
                              List<CommentResponseDto> replies, Long likesCount, boolean liked) {
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
        this.liked = liked;
    }

    /**
     * FeedComment 엔티티에서 DTO로 변환 (대댓글 제외).
     *
     * @param comment           FeedComment 엔티티
     * @param profilePictureUrl 작성자 프로필 사진 URL
     * @param memberName        작성자 이름
     * @param likesCount        댓글 좋아요 수
     * @param liked             현재 사용자가 좋아요 했는지 여부
     */
    public CommentResponseDto(FeedComment comment, String profilePictureUrl, String memberName, Long likesCount, boolean liked) {
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
        this.liked = liked;
    }

    /**
     * FeedComment 엔티티에서 DTO로 변환 (대댓글 포함).
     *
     * @param comment           FeedComment 엔티티
     * @param profilePictureUrl 작성자 프로필 사진 URL
     * @param includeReplies    대댓글 포함 여부 (true이면 대댓글도 매핑)
     * @param memberName        작성자 이름
     * @param likesCount        댓글 좋아요 수
     * @param liked             현재 사용자가 좋아요 했는지 여부
     */
    public CommentResponseDto(FeedComment comment, String profilePictureUrl, boolean includeReplies, String memberName, Long likesCount, boolean liked) {
        this(comment, profilePictureUrl, memberName, likesCount, liked);
        this.replies = includeReplies && comment.getReplies() != null
                ? comment.getReplies().stream()
                .map(reply -> new CommentResponseDto(
                        reply,
                        profilePictureUrl, // 동일 프로필 사진 (실제 상황에서는 각 대댓글마다 가져와야 함)
                        true,
                        memberName,
                        likesCount, // 예시로 전달; 실제 좋아요 수는 개별로 계산 필요
                        false // 기본값 false; 재귀 호출시 currentUser 정보가 필요하면 별도 처리
                ))
                .collect(Collectors.toList())
                : List.of();
    }
}
