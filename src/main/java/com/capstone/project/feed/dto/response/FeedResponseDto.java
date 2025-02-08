package com.capstone.project.feed.dto.response;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 피드 응답 데이터 전송 객체.
 * 이 DTO는 피드 데이터를 클라이언트로 전송할 때 사용되며,
 * 피드 내용, 작성자 정보, 좋아요 수 등과 함께 현재 사용자가 해당 피드를 좋아요 했는지를 나타내는 liked 필드를 포함한다.
 *
 * 순환참조를 방지하기 위해 @JsonIdentityInfo를 사용한다.
 */
@Getter
@Setter
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "feedId")
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
    private String authorName;          // 작성자 이름
    private boolean liked;              // 현재 사용자가 이 피드를 좋아요 했는지 여부

    /**
     * 기본 생성자 (리포스트 관련 없이 기본 필드만 설정).
     *
     * @param feedId             피드 ID
     * @param memberId           작성자 ID
     * @param content            피드 내용
     * @param createdAt          피드 생성 시간
     * @param updatedAt          피드 마지막 수정 시간
     * @param likesCount         좋아요 수
     * @param repostedFrom       리포스트된 피드 ID
     * @param repostedFromContent 리포스트된 피드 내용
     * @param comments           댓글 리스트
     * @param profilePictureUrl  작성자 프로필 사진 URL
     * @param authorName         작성자 이름
     * @param liked              현재 사용자가 좋아요 했는지 여부
     */
    public FeedResponseDto(Integer feedId, Integer memberId, String content, LocalDateTime createdAt,
                           LocalDateTime updatedAt, Integer likesCount, Integer repostedFrom,
                           String repostedFromContent, List<CommentResponseDto> comments,
                           String profilePictureUrl, String authorName, boolean liked) {
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
        this.authorName = authorName;
        this.reposterId = null;
        this.isRepost = false;
        this.repostCreatedAt = null;
        this.mediaUrl = null;
        this.originalPoster = null;
        this.liked = liked;
    }

    /**
     * 리포스트 관련 필드를 포함하는 생성자.
     *
     * @param feedId             피드 ID
     * @param memberId           작성자 ID
     * @param content            피드 내용
     * @param createdAt          피드 생성 시간
     * @param updatedAt          피드 수정 시간
     * @param likesCount         좋아요 수
     * @param repostedFrom       리포스트된 피드 ID
     * @param repostedFromContent 리포스트된 피드 내용
     * @param reposterId         리포스터 ID
     * @param isRepost           리포스트 여부
     * @param comments           댓글 리스트
     * @param repostCreatedAt    리포스트 생성 시간
     * @param mediaUrl           미디어 URL
     * @param originalPoster     원본 게시자 정보
     * @param profilePictureUrl  작성자 프로필 사진 URL
     * @param authorName         작성자 이름
     * @param liked              현재 사용자가 좋아요 했는지 여부
     */
    public FeedResponseDto(Integer feedId, Integer memberId, String content, LocalDateTime createdAt,
                           LocalDateTime updatedAt, Integer likesCount, Integer repostedFrom,
                           String repostedFromContent, Integer reposterId, boolean isRepost,
                           List<CommentResponseDto> comments, LocalDateTime repostCreatedAt,
                           String mediaUrl, MemberInfoDto originalPoster, String profilePictureUrl,
                           String authorName, boolean liked) {
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
        this.authorName = authorName;
        this.liked = liked;
    }
}
