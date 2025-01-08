package com.capstone.project.feed.service;

import com.capstone.project.feed.dto.request.FeedRequestDto;
import com.capstone.project.feed.dto.response.FeedResponseDto;
import com.capstone.project.feed.dto.response.CommentResponseDto;
import com.capstone.project.feed.entity.Feed;
import com.capstone.project.feed.entity.FeedComment;
import com.capstone.project.feed.entity.FeedLike;
import com.capstone.project.feed.entity.SavedPost;
import com.capstone.project.feed.repository.FeedLikeRepository;
import com.capstone.project.feed.repository.FeedRepository;
import com.capstone.project.feed.repository.SavedPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// 피드 서비스
@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedRepository feedRepository; // 피드 리포지토리
    private final SavedPostRepository savedPostRepository; // 저장된 피드 리포지토리
    private final FeedLikeRepository feedLikeRepository; // 피드 좋아요 리포지토리

    // 피드 생성
    @Transactional
    public FeedResponseDto createFeed(FeedRequestDto requestDto) {
        Feed feed = Feed.builder()
                .memberId(requestDto.getMemberId())
                .content(requestDto.getContent())
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .likesCount(0)
                .build();
        Feed savedFeed = feedRepository.save(feed);
        return mapToResponseDto(savedFeed);
    }

    // 피드 수정
    @Transactional
    public FeedResponseDto editFeed(Integer feedId, FeedRequestDto requestDto) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new IllegalArgumentException("Feed not found with ID: " + feedId));
        feed.setContent(requestDto.getContent());
        feed.setUpdatedAt(java.time.LocalDateTime.now());
        return mapToResponseDto(feedRepository.save(feed));
    }

    // 피드 삭제
    @Transactional
    public void deleteFeed(Integer feedId) {
        feedRepository.deleteById(feedId);
    }

    // 리포스트 생성
    @Transactional
    public FeedResponseDto repostFeed(Integer originalFeedId, Integer reposterId) {
        Feed originalFeed = feedRepository.findById(originalFeedId)
                .orElseThrow(() -> new IllegalArgumentException("Feed not found with ID: " + originalFeedId));

        Feed repost = Feed.builder()
                .memberId(reposterId)
                .content(originalFeed.getContent())
                .repostedFrom(originalFeed)
                .repostedFromContent(originalFeed.getContent())
                .reposterId(reposterId)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .likesCount(0)
                .build();

        feedRepository.save(repost);
        return mapToResponseDto(repost);
    }

    // 전체 피드 조회 (댓글 대댓글 포함)
    @Transactional(readOnly = true)
    public List<FeedResponseDto> getAllFeeds() {
        return feedRepository.findAll()
                .stream()
                .map(feed -> {
                    // Feed를 FeedResponseDto로 변환
                    FeedResponseDto feedResponse = mapToResponseDto(feed);

                    // 댓글 목록을 부모 댓글로 필터링하고 대댓글 포함하여 정리
                    List<CommentResponseDto> commentsWithReplies = feed.getComments().stream()
                            .filter(comment -> comment.getParentComment() == null) // 부모 댓글만 처리
                            .map(this::mapCommentWithReplies) // 대댓글 포함 매핑
                            .collect(Collectors.toList());

                    feedResponse.setComments(commentsWithReplies); // 정리된 댓글 설정
                    return feedResponse;
                })
                .collect(Collectors.toList());
    }

    // 특정 피드 조회 (댓글 포함)
    @Transactional(readOnly = true)
    public FeedResponseDto getFeedById(Integer feedId) {
        // 피드 조회 (예외 처리 포함)
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new IllegalArgumentException("Feed not found with ID: " + feedId));

        // FeedResponseDto 변환
        FeedResponseDto feedResponse = mapToResponseDto(feed);

        // 댓글 목록을 부모 댓글로 필터링하고 대댓글 포함하여 정리
        List<CommentResponseDto> commentsWithReplies = feed.getComments().stream()
                .filter(comment -> comment.getParentComment() == null) // 부모 댓글만 처리
                .map(this::mapCommentWithReplies) // 대댓글 포함 매핑
                .collect(Collectors.toList());

        feedResponse.setComments(commentsWithReplies); // 정리된 댓글 설정
        return feedResponse;
    }

    // 대댓글 포함 댓글 매핑
    private CommentResponseDto mapCommentWithReplies(FeedComment comment) {
        // FeedComment -> CommentResponseDto 변환
        CommentResponseDto commentResponse = new CommentResponseDto(comment);

        // 대댓글 처리
        List<CommentResponseDto> replies = comment.getReplies().stream()
                .map(this::mapCommentWithReplies) // 재귀적으로 대댓글 매핑
                .collect(Collectors.toList());

        commentResponse.setReplies(replies); // 대댓글 설정
        return commentResponse;
    }

    // Feed -> FeedResponseDto 변환
    private FeedResponseDto mapToResponseDto(Feed feed) {
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
                .isRepost(feed.isRepost())
                .comments(null) // 댓글은 이후 단계에서 설정
                .build();
    }

    // 특정 회원의 저장된 게시물 조회
    @Transactional(readOnly = true)
    public List<FeedResponseDto> getSavedPostsByMemberId(Integer memberId) {
        List<SavedPost> savedPosts = savedPostRepository.findByMemberId(memberId);
        return savedPosts.stream()
                .map(savedPost -> {
                    Feed feed = savedPost.getFeed();
                    List<CommentResponseDto> commentsWithReplies = feed.getComments().stream()
                            .filter(comment -> comment.getParentComment() == null)
                            .map(this::mapCommentWithReplies)
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
                            .isRepost(feed.isRepost())
                            .comments(commentsWithReplies)
                            .build();
                })
                .collect(Collectors.toList());
    }

    // 피드 좋아요 추가
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

    // 피드 좋아요 취소
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
