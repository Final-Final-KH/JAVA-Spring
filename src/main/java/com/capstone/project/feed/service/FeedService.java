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

// 피드 서비스
@Service
@Slf4j
@RequiredArgsConstructor
public class FeedService {
    private final FeedRepository feedRepository; // 피드 리포지토리
    private final SavedPostRepository savedPostRepository; // 저장된 피드 리포지토리
    private final FeedLikeRepository feedLikeRepository; // 피드 좋아요 리포지토리
    private final MemberService memberService; // 멤버 서비스
    private final CommentLikeRepository commentLikeRepository; // 댓글 좋아요 리포지토리


    // 피드 생성
    @Transactional
    public FeedResponseDto createFeed(FeedRequestDto requestDto) {
        Feed feed = Feed.builder()
                .memberId(requestDto.getMemberId())
                .content(requestDto.getContent())
                .mediaUrl(requestDto.getMediaUrl()) // 미디어 URL 추가
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
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
        feed.setUpdatedAt(LocalDateTime.now());
        return mapToResponseDto(feedRepository.save(feed));
    }

    // 피드 삭제
    @Transactional
    public void deleteFeed(Integer feedId) {
        feedRepository.deleteById(feedId);
    }

    // 리포스트 생성
    @Transactional
    public FeedResponseDto repostFeed(Integer originalFeedId, Integer reposterId, RepostRequestDto requestDto) {
        // 원본 피드를 조회합니다. 존재하지 않을 경우 예외를 발생시킵니다.
        Feed originalFeed = feedRepository.findById(originalFeedId)
                .orElseThrow(() -> new IllegalArgumentException("Feed not found with ID: " + originalFeedId));

        // 리포스트 내용이 요청 DTO에 제공되었는지 확인하고, 없다면 원본 피드 내용을 사용.
        String repostContent = (requestDto.getContent() != null && !requestDto.getContent().isEmpty())
                ? requestDto.getContent()
                : originalFeed.getContent();

        // 새로운 리포스트 피드 객체를 생성.
        Feed repost = Feed.builder()
                .memberId(reposterId) // 리포스터(재게시하는 사용자) ID 설정
                .content(repostContent) // 리포스트 내용 설정
                .repostedFrom(originalFeed) // 원본 피드 참조
                .repostedFromContent(originalFeed.getContent()) // 원본 피드의 내용 저장
                .reposterId(reposterId) // 리포스터 ID 설정
                .repostCreatedAt(LocalDateTime.now()) // 리포스트 생성 시간 설정
                .mediaUrl(requestDto.getMediaUrl() != null ? requestDto.getMediaUrl() : originalFeed.getMediaUrl()) // 미디어 URL 설정 (없다면 원본 피드의 URL 사용)
                .createdAt(LocalDateTime.now()) // 리포스트 생성 시간 설정
                .updatedAt(LocalDateTime.now()) // 리포스트 업데이트 시간 설정
                .likesCount(0) // 초기 좋아요 수는 0으로 설정
                .build();

        // 새롭게 생성된 리포스트 피드를 데이터베이스에 저장.
        feedRepository.save(repost);

        // 저장된 리포스트 피드를 DTO로 변환하여 반환.
        return mapToResponseDto(repost);
    }

    // 전체 피드 조회 (댓글 대댓글 포함)
    @Transactional(readOnly = true)
    public List<FeedResponseDto> getAllFeeds() {
        return feedRepository.findAll()
                .stream()
                .map(feed -> {
                    FeedResponseDto feedResponse = mapToResponseDto(feed);

                    // 댓글 목록을 부모 댓글로 필터링하고 대댓글 포함하여 정리
                    List<CommentResponseDto> commentsWithReplies = feed.getComments().stream()
                            .filter(comment -> comment.getParentComment() == null)
                            .map(this::mapCommentWithReplies)
                            .collect(Collectors.toList());

                    feedResponse.setComments(commentsWithReplies);
                    return feedResponse;
                })
                .collect(Collectors.toList());
    }

    // 특정 피드 조회 (댓글 포함)
    @Transactional(readOnly = true)
    public FeedResponseDto getFeedById(Integer feedId) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new IllegalArgumentException("Feed not found with ID: " + feedId));

        FeedResponseDto feedResponse = mapToResponseDto(feed);

        List<CommentResponseDto> commentsWithReplies = feed.getComments().stream()
                .filter(comment -> comment.getParentComment() == null)
                .map(this::mapCommentWithReplies)
                .collect(Collectors.toList());

        feedResponse.setComments(commentsWithReplies);
        return feedResponse;
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
                            .repostCreatedAt(feed.getRepostCreatedAt())
                            .mediaUrl(feed.getMediaUrl())
                            .comments(commentsWithReplies)
                            .isRepost(feed.isRepost())
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

    // Feed -> FeedResponseDto 변환 메서드 (FeedService 내부)
    private FeedResponseDto mapToResponseDto(Feed feed) {
        MemberInfoDto originalPoster = null;
        // 리포스트일 경우, 원본 작성자의 정보를 가져옵니다.
        if (feed.getRepostedFrom() != null) {
            Feed originalFeed = feed.getRepostedFrom();
            originalPoster = getMemberInfoById(originalFeed.getMemberId());
        }

        // 피드를 작성한 멤버의 프로필 정보를 가져옵니다.
        MemberResponseDto memberResponse = memberService.getMemberProfile(feed.getMemberId());
        String profilePictureUrl = memberResponse.getProfilePictureUrl(); // 프로필 사진 URL 가져오기
        String authorName = memberResponse.getName(); // 작성자 이름 가져오기

        log.info("Mapping FeedResponseDto: feedId={}, memberId={}, profilePictureUrl={}, authorName={}, originalPoster={}",
                feed.getFeedId(), feed.getMemberId(), profilePictureUrl, authorName, originalPoster);

        // 피드에 포함된 댓글(대댓글 포함)을 매핑합니다.
        List<CommentResponseDto> commentsWithReplies = feed.getComments().stream()
                .filter(comment -> comment.getParentComment() == null) // 부모 댓글 필터링
                .map(this::mapCommentWithReplies) // 댓글과 대댓글 매핑
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
                .authorName(authorName) // 새 필드 설정
                .comments(commentsWithReplies)
                .isRepost(feed.isRepost())
                .build();
    }



    // 멤버 정보 가져오기
    private MemberInfoDto getMemberInfoById(Integer memberId) {
        try {
            MemberResponseDto memberResponse = memberService.getMemberProfile(memberId); // 멤버 프로필 호출
            log.info("Fetching member info: memberId={}, profilePictureUrl={}",
                    memberId, memberResponse.getProfilePictureUrl());
            return MemberInfoDto.builder()
                    .memberId(memberResponse.getMemberId())
                    .name(memberResponse.getName())
                    .profilePictureUrl(memberResponse.getProfilePictureUrl()) // 프로필 사진 URL 추가
                    .build();
        } catch (EntityNotFoundException e) {
            return null; // 멤버를 찾지 못했을 경우 null 반환
        }
    }

    // 댓글과 대댓글을 매핑하는 메서드
    private CommentResponseDto mapCommentWithReplies(FeedComment comment) {
        // 작성자 프로필 사진 URL 가져오기
        String profilePictureUrl = memberService.getMemberProfile(comment.getMemberId()).getProfilePictureUrl();
        if (profilePictureUrl == null || profilePictureUrl.isEmpty()) {
            profilePictureUrl = "https://firebasestorage.googleapis.com/v0/b/kh-react-firebase.firebasestorage.app/o/default-profile-picture-url.jpg?alt=media&token=16b39451-4ee9-4bdd-adc9-78b6cda4d4bb";
        }

        // 좋아요 수 가져오기
        Long likesCount = commentLikeRepository.countByComment_CommentId(comment.getCommentId());

        // 작성자 이름(memberName) 가져오기
        String memberName = memberService.getMemberProfile(comment.getMemberId()).getName();

        // 디버깅용 로그 출력
        log.info("Mapping commentId: {}, memberId: {}, profilePictureUrl: {}, memberName: {}, likesCount: {}",
                comment.getCommentId(), comment.getMemberId(), profilePictureUrl, memberName, likesCount);

        // 대댓글들을 재귀적으로 매핑
        List<CommentResponseDto> replies = comment.getReplies().stream()
                .map(this::mapCommentWithReplies)
                .collect(Collectors.toList());

        // CommentResponseDto 생성
        // 주의: 새 생성자는 다음과 같이 호출합니다.
        // new CommentResponseDto(FeedComment, String profilePictureUrl, boolean includeReplies, String memberName, Long likesCount)
        return new CommentResponseDto(comment, profilePictureUrl, true, memberName, likesCount)
                .toBuilder()
                .replies(replies)
                .build();
    }


}
