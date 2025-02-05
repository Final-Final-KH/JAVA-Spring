package com.capstone.project.feed.service;

import com.capstone.project.feed.dto.request.FeedRequestDto;
import com.capstone.project.feed.dto.request.RepostRequestDto;
import com.capstone.project.feed.dto.response.FeedResponseDto;
import com.capstone.project.feed.dto.response.CommentResponseDto;
import com.capstone.project.feed.dto.response.MemberInfoDto;
import com.capstone.project.feed.entity.Feed;
import com.capstone.project.feed.entity.FeedComment;
import com.capstone.project.feed.entity.FeedLike;
import com.capstone.project.feed.entity.SavedPost;
import com.capstone.project.feed.repository.CommentLikeRepository;
import com.capstone.project.feed.repository.FeedLikeRepository;
import com.capstone.project.feed.repository.FeedRepository;
import com.capstone.project.feed.repository.SavedPostRepository;
import com.capstone.project.member.dto.response.MemberResponseDto;
import com.capstone.project.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 피드 서비스 클래스.
 * 피드 생성, 수정, 삭제, 리포스트, 좋아요 처리 등 피드와 관련된 모든 비즈니스 로직을 처리한다.
 *
 * KR: 여기서는 댓글 및 대댓글의 매핑 시 항상 재귀적으로 mapCommentWithReplies() 메소드를 호출하여
 *     각 댓글의 좋아요 수와 좋아요 상태를 정확하게 계산하도록 한다.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FeedService {
    private final FeedRepository feedRepository;
    private final SavedPostRepository savedPostRepository;
    private final FeedLikeRepository feedLikeRepository;
    private final MemberService memberService;
    private final CommentLikeRepository commentLikeRepository;

    /**
     * 피드를 생성한다.
     *
     * @param requestDto 피드 생성 요청 DTO
     * @return 생성된 피드를 FeedResponseDto로 반환
     */
    @Transactional
    public FeedResponseDto createFeed(FeedRequestDto requestDto) {
        Feed feed = Feed.builder()
                .memberId(requestDto.getMemberId())
                .content(requestDto.getContent())
                .mediaUrl(requestDto.getMediaUrl())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .likesCount(0)
                .build();

        Feed savedFeed = feedRepository.save(feed);
        // 생성 시점에는 아직 좋아요가 없으므로 liked=false
        return mapToResponseDto(savedFeed, requestDto.getMemberId());
    }

    /**
     * 피드를 수정한다.
     *
     * @param feedId     수정할 피드 ID
     * @param requestDto 수정 요청 DTO
     * @return 수정된 피드를 FeedResponseDto로 반환
     */
    @Transactional
    public FeedResponseDto editFeed(Integer feedId, FeedRequestDto requestDto) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new IllegalArgumentException("Feed not found with ID: " + feedId));
        feed.setContent(requestDto.getContent());
        feed.setUpdatedAt(LocalDateTime.now());
        return mapToResponseDto(feedRepository.save(feed), requestDto.getMemberId());
    }

    /**
     * 피드를 삭제한다.
     *
     * @param feedId 삭제할 피드 ID
     */
    @Transactional
    public void deleteFeed(Integer feedId) {
        feedRepository.deleteById(feedId);
    }

    /**
     * 리포스트를 생성한다.
     *
     * @param originalFeedId 리포스트할 원본 피드 ID
     * @param reposterId     리포스터(게시글을 재게시하는 사용자) ID
     * @param requestDto     리포스트 요청 DTO
     * @return 생성된 리포스트를 FeedResponseDto로 반환
     */
    @Transactional
    public FeedResponseDto repostFeed(Integer originalFeedId, Integer reposterId, RepostRequestDto requestDto) {
        Feed originalFeed = feedRepository.findById(originalFeedId)
                .orElseThrow(() -> new IllegalArgumentException("Feed not found with ID: " + originalFeedId));

        String repostContent = (requestDto.getContent() != null && !requestDto.getContent().isEmpty())
                ? requestDto.getContent()
                : originalFeed.getContent();

        Feed repost = Feed.builder()
                .memberId(reposterId)
                .content(repostContent)
                .repostedFrom(originalFeed)
                .repostedFromContent(originalFeed.getContent())
                .reposterId(reposterId)
                .repostCreatedAt(LocalDateTime.now())
                .mediaUrl(requestDto.getMediaUrl() != null ? requestDto.getMediaUrl() : originalFeed.getMediaUrl())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .likesCount(0)
                .build();

        feedRepository.save(repost);
        // 리포스트 시에도 기본적으로 좋아요가 없으므로 liked=false
        return mapToResponseDto(repost, reposterId);
    }

    /**
     * 전체 피드를 조회한다.
     * (현재 사용자(memberId)를 받아서 각 피드에 대해 좋아요 여부(liked)를 설정한다.)
     *
     * @param currentMemberId 현재 사용자 ID
     * @return 모든 피드를 FeedResponseDto 리스트로 반환
     */
    @Transactional(readOnly = true)
    public List<FeedResponseDto> getAllFeeds(Integer currentMemberId) {
        return feedRepository.findAll()
                .stream()
                .map(feed -> {
                    FeedResponseDto feedResponse = mapToResponseDto(feed, currentMemberId);
                    // KR: 댓글 매핑 시 재귀적으로 mapCommentWithReplies()를 호출하여 대댓글까지 정확히 매핑한다.
                    List<CommentResponseDto> commentsWithReplies = feed.getComments().stream()
                            .filter(comment -> comment.getParentComment() == null)
                            .map(comment -> mapCommentWithReplies(comment, currentMemberId))
                            .collect(Collectors.toList());
                    feedResponse.setComments(commentsWithReplies);
                    return feedResponse;
                })
                .collect(Collectors.toList());
    }

    /**
     * 특정 피드를 조회한다.
     * (현재 사용자(memberId)를 받아서 좋아요 여부(liked)를 설정한다.)
     *
     * @param feedId          피드 ID
     * @param currentMemberId 현재 사용자 ID
     * @return 해당 피드를 FeedResponseDto로 반환
     */
    @Transactional(readOnly = true)
    public FeedResponseDto getFeedById(Integer feedId, Integer currentMemberId) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new IllegalArgumentException("Feed not found with ID: " + feedId));

        FeedResponseDto feedResponse = mapToResponseDto(feed, currentMemberId);

        List<CommentResponseDto> commentsWithReplies = feed.getComments().stream()
                .filter(comment -> comment.getParentComment() == null)
                .map(comment -> mapCommentWithReplies(comment, currentMemberId))
                .collect(Collectors.toList());

        feedResponse.setComments(commentsWithReplies);
        return feedResponse;
    }

    /**
     * 특정 회원의 저장된 게시물을 조회한다.
     *
     * @param memberId 조회할 회원 ID
     * @return 저장된 게시물을 FeedResponseDto 리스트로 반환
     */
    @Transactional(readOnly = true)
    public List<FeedResponseDto> getSavedPostsByMemberId(Integer memberId) {
        List<SavedPost> savedPosts = savedPostRepository.findByMemberId(memberId);
        return savedPosts.stream()
                .map(savedPost -> {
                    Feed feed = savedPost.getFeed();
                    List<CommentResponseDto> commentsWithReplies = feed.getComments().stream()
                            .filter(comment -> comment.getParentComment() == null)
                            .map(comment -> mapCommentWithReplies(comment, memberId))
                            .collect(Collectors.toList());

                    return FeedResponseDto.builder()
                            .feedId(feed.getFeedId())
                            .memberId(feed.getMemberId())
                            .content(feed.getContent())
                            .createdAt(feed.getCreatedAt())
                            .updatedAt(feed.getUpdatedAt())
                            .likesCount(feed.getLikesCount())
                            .repostedFrom(feed.getRepostedFrom() != null ? feed.getRepostedFrom().getFeedId() : null)
                            .repostedFromContent(feed.getRepostedFrom() != null ? feed.getRepostedFrom().getContent() : null)
                            .reposterId(feed.getReposterId())
                            .repostCreatedAt(feed.getRepostCreatedAt())
                            .mediaUrl(feed.getMediaUrl())
                            .comments(commentsWithReplies)
                            .isRepost(feed.isRepost())
                            .profilePictureUrl(memberService.getMemberProfile(feed.getMemberId()).getProfilePictureUrl())
                            .authorName(memberService.getMemberProfile(feed.getMemberId()).getName())
                            .liked(feedLikeRepository.existsByFeed_FeedIdAndMemberId(feed.getFeedId(), memberId))
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * Feed 엔티티를 FeedResponseDto로 변환한다.
     * 현재 사용자(currentMemberId)를 고려하여 좋아요 여부(liked)를 설정한다.
     *
     * KR: 여기서 댓글들은 모두 mapCommentWithReplies() 메소드를 통해 재귀적으로 매핑된다.
     *
     * @param feed            Feed 엔티티
     * @param currentMemberId 현재 사용자 ID
     * @return FeedResponseDto 객체
     */
    private FeedResponseDto mapToResponseDto(Feed feed, Integer currentMemberId) {
        MemberInfoDto originalPoster = null;
        if (feed.getRepostedFrom() != null) {
            Feed originalFeed = feed.getRepostedFrom();
            originalPoster = getMemberInfoById(originalFeed.getMemberId());
        }

        MemberResponseDto memberResponse = memberService.getMemberProfile(feed.getMemberId());
        String profilePictureUrl = memberResponse.getProfilePictureUrl();
        String authorName = memberResponse.getName();

        // 현재 사용자가 피드를 좋아요 했는지 여부
        boolean liked = currentMemberId != null &&
                feedLikeRepository.existsByFeed_FeedIdAndMemberId(feed.getFeedId(), currentMemberId);

        // 댓글 매핑: 부모 댓글만 필터링한 후 재귀적으로 대댓글까지 매핑
        List<CommentResponseDto> commentsWithReplies = feed.getComments().stream()
                .filter(comment -> comment.getParentComment() == null)
                .map(comment -> mapCommentWithReplies(comment, currentMemberId))
                .collect(Collectors.toList());

        return FeedResponseDto.builder()
                .feedId(feed.getFeedId())
                .memberId(feed.getMemberId())
                .content(feed.getContent())
                .createdAt(feed.getCreatedAt())
                .updatedAt(feed.getUpdatedAt())
                .likesCount(feed.getLikesCount())
                .repostedFrom(feed.getRepostedFrom() != null ? feed.getRepostedFrom().getFeedId() : null)
                .repostedFromContent(feed.getRepostedFrom() != null ? feed.getRepostedFrom().getContent() : null)
                .reposterId(feed.getReposterId())
                .repostCreatedAt(feed.getRepostCreatedAt())
                .mediaUrl(feed.getMediaUrl())
                .profilePictureUrl(profilePictureUrl)
                .originalPoster(originalPoster)
                .authorName(authorName)
                .comments(commentsWithReplies)
                .isRepost(feed.isRepost())
                .liked(liked)
                .build();
    }

    /**
     * 재귀적으로 댓글(및 대댓글)을 DTO로 변환한다.
     * 현재 사용자(currentMemberId)를 고려하여 좋아요 여부(liked)를 설정한다.
     *
     * KR: 이 메소드는 각 댓글에 대해 프로필 사진, 이름, 좋아요 수, 좋아요 여부를 새로 계산하며,
     *     만약 해당 댓글에 대댓글(replies)이 있다면 재귀적으로 동일한 처리를 진행한다.
     *
     * @param comment         FeedComment 엔티티
     * @param currentMemberId 현재 사용자 ID
     * @return CommentResponseDto 객체
     */
    private CommentResponseDto mapCommentWithReplies(FeedComment comment, Integer currentMemberId) {
        // 작성자 프로필 사진 및 이름 조회 (기본값 제공)
        String profilePictureUrl = memberService.getMemberProfile(comment.getMemberId()).getProfilePictureUrl();
        if (profilePictureUrl == null || profilePictureUrl.isEmpty()) {
            profilePictureUrl = "https://example.com/default-profile-picture.jpg";
        }
        String memberName = memberService.getMemberProfile(comment.getMemberId()).getName();

        // 해당 댓글의 좋아요 수 계산
        Long likesCount = commentLikeRepository.countByComment_CommentId(comment.getCommentId());
        // 현재 사용자가 이 댓글을 좋아요 했는지 여부 계산
        boolean liked = currentMemberId != null &&
                commentLikeRepository.existsByComment_CommentIdAndMemberId(comment.getCommentId(), currentMemberId);

        // 재귀적으로 대댓글(자식 댓글) 매핑
        List<CommentResponseDto> replies = comment.getReplies().stream()
                .map(reply -> mapCommentWithReplies(reply, currentMemberId))
                .collect(Collectors.toList());

        return CommentResponseDto.builder()
                .commentId(comment.getCommentId())
                .feedId(comment.getFeed().getFeedId())
                .memberId(comment.getMemberId())
                .memberName(memberName)
                .comment(comment.getComment())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .profilePictureUrl(profilePictureUrl)
                .replies(replies)
                .likesCount(likesCount)
                .liked(liked)
                .build();
    }

    /**
     * 멤버 정보를 가져온다.
     *
     * @param memberId 회원 ID
     * @return MemberInfoDto 객체 (없으면 null 반환)
     */
    private MemberInfoDto getMemberInfoById(Integer memberId) {
        try {
            MemberResponseDto memberResponse = memberService.getMemberProfile(memberId);
            return MemberInfoDto.builder()
                    .memberId(memberResponse.getMemberId())
                    .name(memberResponse.getName())
                    .profilePictureUrl(memberResponse.getProfilePictureUrl())
                    .build();
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    // 피드 좋아요 추가 및 취소 메서드는 그대로 유지한다.
    @Transactional
    public void likeFeed(Integer feedId, Integer memberId) {
        if (feedLikeRepository.existsByFeed_FeedIdAndMemberId(feedId, memberId)) {
            throw new IllegalStateException("이미 좋아요를 눌렀습니다.");
        }

        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new IllegalArgumentException("피드를 찾을 수 없습니다. ID: " + feedId));

        FeedLike feedLike = FeedLike.builder()
                .feed(feed)
                .memberId(memberId)
                .build();
        feedLikeRepository.save(feedLike);

        feed.setLikesCount(feed.getLikesCount() + 1);
        feedRepository.save(feed);
    }

    @Transactional
    public void unlikeFeed(Integer feedId, Integer memberId) {
        FeedLike feedLike = feedLikeRepository.findByFeed_FeedIdAndMemberId(feedId, memberId)
                .orElseThrow(() -> new IllegalStateException("좋아요를 찾을 수 없습니다."));

        feedLikeRepository.delete(feedLike);

        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new IllegalArgumentException("피드를 찾을 수 없습니다. ID: " + feedId));
        feed.setLikesCount(feed.getLikesCount() - 1);
        feedRepository.save(feed);
    }
}
