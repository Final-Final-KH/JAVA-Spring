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
    private Integer reposterId;         // 리포스터 ID (nullable)
    private boolean isRepost;           // 리포스트 여부
    private List<CommentResponseDto> comments; // 해당 피드의 댓글 리스트
    private LocalDateTime repostCreatedAt; // 리포스트된 시간
    private String mediaUrl;            // 미디어 파일 URL
    private MemberInfoDto originalPoster; // 원본 게시자 정보 (리포스트일 경우)
    private String profilePictureUrl;   // 작성자의 프로필 사진 URL
    private String authorName;          // 작성자 이름 (Option 2: 추가된 필드)

    // 기본 생성자 (기존 기능 유지)
    public FeedResponseDto(Integer feedId, Integer memberId, String content, LocalDateTime createdAt,
                           LocalDateTime updatedAt, Integer likesCount, Integer repostedFrom,
                           String repostedFromContent, List<CommentResponseDto> comments,
                           String profilePictureUrl, String authorName) {
        this.feedId = feedId;
        this.memberId = memberId;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.likesCount = likesCount;
        this.repostedFrom = repostedFrom;
        this.repostedFromContent = repostedFromContent;
        this.comments = comments;
        this.profilePictureUrl = profilePictureUrl;
        this.authorName = authorName; // 새 필드 할당
        this.reposterId = null; // 초기값 null
        this.isRepost = false; // 기본 false
        this.repostCreatedAt = null; // 기본 null
        this.mediaUrl = null; // 기본 null
        this.originalPoster = null; // 기본 null
    }

    // 리포스트 관련 필드를 포함하는 생성자
    public FeedResponseDto(Integer feedId, Integer memberId, String content, LocalDateTime createdAt,
                           LocalDateTime updatedAt, Integer likesCount, Integer repostedFrom,
                           String repostedFromContent, Integer reposterId, boolean isRepost,
                           List<CommentResponseDto> comments, LocalDateTime repostCreatedAt,
                           String mediaUrl, MemberInfoDto originalPoster, String profilePictureUrl,
                           String authorName) {
        this.feedId = feedId;
        this.memberId = memberId;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.likesCount = likesCount;
        this.repostedFrom = repostedFrom;
        this.repostedFromContent = repostedFromContent;
        this.reposterId = reposterId;
        this.isRepost = isRepost;
        this.comments = comments;
        this.repostCreatedAt = repostCreatedAt;
        this.mediaUrl = mediaUrl;
        this.originalPoster = originalPoster;
        this.profilePictureUrl = profilePictureUrl;
        this.authorName = authorName; // 새 필드 할당
    }
}
