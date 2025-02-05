package com.capstone.project.feed.service;

import com.capstone.project.feed.dto.request.CommentRequestDto;
import com.capstone.project.feed.dto.response.CommentResponseDto;
import com.capstone.project.feed.entity.Feed;
import com.capstone.project.feed.entity.FeedComment;
import com.capstone.project.feed.entity.CommentLike;
import com.capstone.project.feed.repository.FeedCommentRepository;
import com.capstone.project.feed.repository.CommentLikeRepository;
import com.capstone.project.feed.repository.FeedRepository;
import com.capstone.project.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 댓글 서비스
 *
 * 이 서비스는 댓글(및 대댓글)의 추가, 수정, 삭제, 조회 및 좋아요 처리 기능을 제공한다.
 * 각 댓글을 DTO로 변환할 때 작성자 이름(memberName)과 프로필 사진 URL을 포함시켜 반환한다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final FeedRepository feedRepository; // Feed 엔티티와 상호작용하는 리포지토리
    private final FeedCommentRepository feedCommentRepository; // FeedComment 엔티티와 상호작용하는 리포지토리
    private final CommentLikeRepository commentLikeRepository; // 댓글 좋아요 관련 리포지토리
    private final MemberService memberService; // 멤버 서비스

    /**
     * 댓글 또는 대댓글을 추가한다.
     *
     * @param feedId          댓글이 추가될 피드 ID
     * @param requestDto      댓글 요청 DTO (댓글 내용 및 작성자 ID 포함)
     * @param parentCommentId 부모 댓글 ID (대댓글일 경우; 없으면 null)
     * @return 생성된 댓글의 CommentResponseDto
     */
    public CommentResponseDto addComment(Integer feedId, CommentRequestDto requestDto, Integer parentCommentId) {
        // 피드 유효성 검증
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new EntityNotFoundException("Feed not found with ID: " + feedId));

        FeedComment parentComment = null;
        if (parentCommentId != null) {
            // 부모 댓글 유효성 검증
            parentComment = feedCommentRepository.findById(parentCommentId)
                    .orElseThrow(() -> new EntityNotFoundException("Parent comment not found with ID: " + parentCommentId));
        }

        // FeedComment 엔티티 생성 및 저장
        FeedComment comment = FeedComment.builder()
                .feed(feed)
                .memberId(requestDto.getMemberId())
                .comment(requestDto.getComment())
                .parentComment(parentComment)
                .build();

        feedCommentRepository.save(comment);
        log.info("Comment added successfully: {}", comment);

        // 작성자 프로필 사진 URL과 작성자 이름 가져오기
        String profilePictureUrl = memberService.getMemberProfile(comment.getMemberId()).getProfilePictureUrl();
        String memberName = memberService.getMemberProfile(comment.getMemberId()).getName();

        // CommentResponseDto 생성 및 반환
        return new CommentResponseDto(comment, profilePictureUrl, memberName,
                commentLikeRepository.countByComment_CommentId(comment.getCommentId()));
    }

    /**
     * 댓글을 수정한다.
     *
     * @param commentId  수정할 댓글 ID
     * @param requestDto 수정 요청 DTO (새 댓글 내용)
     * @return 수정된 댓글의 CommentResponseDto
     */
    public CommentResponseDto editComment(Integer commentId, CommentRequestDto requestDto) {
        // 댓글 유효성 검증
        FeedComment comment = feedCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with ID: " + commentId));

        // 댓글 내용 수정
        comment.setComment(requestDto.getComment());
        feedCommentRepository.save(comment);
        log.info("Comment edited successfully: {}", comment);

        // 작성자 프로필 사진 URL과 작성자 이름 가져오기
        String profilePictureUrl = memberService.getMemberProfile(comment.getMemberId()).getProfilePictureUrl();
        String memberName = memberService.getMemberProfile(comment.getMemberId()).getName();

        // 수정된 CommentResponseDto 생성 및 반환
        return new CommentResponseDto(comment, profilePictureUrl, memberName,
                commentLikeRepository.countByComment_CommentId(comment.getCommentId()));
    }

    /**
     * 댓글을 삭제한다.
     *
     * @param commentId 삭제할 댓글 ID
     */
    public void deleteComment(Integer commentId) {
        // 댓글과 연관된 좋아요 먼저 삭제
        commentLikeRepository.deleteByComment_CommentId(commentId);

        // 댓글 삭제
        FeedComment comment = feedCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with ID: " + commentId));
        feedCommentRepository.delete(comment);
        log.info("Comment deleted successfully: {}", commentId);
    }

    /**
     * 특정 피드의 부모 댓글(대댓글 제외)들을 조회한다.
     *
     * @param feedId 피드 ID
     * @return 부모 댓글들의 CommentResponseDto 리스트
     */
    public List<CommentResponseDto> getCommentsByFeedId(Integer feedId) {
        // 피드 유효성 검증
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new EntityNotFoundException("Feed not found with ID: " + feedId));

        // 부모 댓글만 조회 후 DTO로 변환
        return feedCommentRepository.findByFeedAndParentCommentIsNull(feed)
                .stream()
                .map(comment -> {
                    String profilePictureUrl = memberService.getMemberProfile(comment.getMemberId()).getProfilePictureUrl();
                    String memberName = memberService.getMemberProfile(comment.getMemberId()).getName();
                    return new CommentResponseDto(comment, profilePictureUrl, memberName,
                            commentLikeRepository.countByComment_CommentId(comment.getCommentId()));
                })
                .collect(Collectors.toList());
    }

    /**
     * 특정 댓글을 조회한다.
     *
     * @param commentId 댓글 ID
     * @return 해당 댓글의 CommentResponseDto (좋아요 수 포함)
     */
    public CommentResponseDto getCommentWithLikes(Integer commentId) {
        // 댓글 유효성 검증
        FeedComment comment = feedCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with ID: " + commentId));

        // 작성자 프로필 사진 URL과 작성자 이름 가져오기
        String profilePictureUrl = memberService.getMemberProfile(comment.getMemberId()).getProfilePictureUrl();
        String memberName = memberService.getMemberProfile(comment.getMemberId()).getName();

        // 좋아요 수 조회 후 DTO 반환
        Long likesCount = commentLikeRepository.countByComment_CommentId(commentId);
        return new CommentResponseDto(comment, profilePictureUrl, memberName, likesCount);
    }

    /**
     * 특정 회원의 모든 댓글(대댓글 포함)을 조회하고, 대댓글에 포함된 댓글은 중복 제거한다.
     *
     * @param memberId 회원 ID
     * @return 중복 제거된 CommentResponseDto 리스트
     */
    public List<CommentResponseDto> getCommentsByMemberId(Integer memberId) {
        // 모든 댓글 조회 후 DTO로 변환
        List<CommentResponseDto> allComments = feedCommentRepository.findByMemberId(memberId).stream()
                .map(comment -> {
                    String profilePictureUrl = memberService.getMemberProfile(comment.getMemberId()).getProfilePictureUrl();
                    String memberName = memberService.getMemberProfile(comment.getMemberId()).getName();
                    return new CommentResponseDto(comment, profilePictureUrl, true, memberName,
                            commentLikeRepository.countByComment_CommentId(comment.getCommentId()));
                })
                .collect(Collectors.toList());

        // 중복 제거: 대댓글에 포함된 댓글의 ID를 수집하여 상위 목록에서 제거
        Set<Integer> replyIds = new HashSet<>();
        allComments.forEach(comment -> collectReplyIds(comment, replyIds));

        return allComments.stream()
                .filter(comment -> !replyIds.contains(comment.getCommentId()))
                .collect(Collectors.toList());
    }

    /**
     * 대댓글의 ID를 재귀적으로 수집한다.
     *
     * @param comment  현재 댓글 DTO
     * @param replyIds 대댓글 ID를 저장할 Set
     */
    private void collectReplyIds(CommentResponseDto comment, Set<Integer> replyIds) {
        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            for (CommentResponseDto reply : comment.getReplies()) {
                replyIds.add(reply.getCommentId());
                collectReplyIds(reply, replyIds);
            }
        }
    }

    /**
     * 댓글 좋아요를 처리한다.
     *
     * @param commentId 댓글 ID
     * @param memberId  좋아요를 누른 회원 ID
     */
    public void likeComment(Integer commentId, Integer memberId) {
        FeedComment comment = feedCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with ID: " + commentId));

        if (commentLikeRepository.existsByComment_CommentIdAndMemberId(commentId, memberId)) {
            throw new IllegalArgumentException("Comment already liked by this member");
        }

        CommentLike like = CommentLike.builder()
                .comment(comment)
                .memberId(memberId)
                .build();

        commentLikeRepository.save(like);
        log.info("Comment liked: commentId={}, memberId={}", commentId, memberId);
    }

    /**
     * 댓글 좋아요 취소를 처리한다.
     *
     * @param commentId 댓글 ID
     * @param memberId  좋아요를 취소한 회원 ID
     */
    public void unlikeComment(Integer commentId, Integer memberId) {
        FeedComment comment = feedCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with ID: " + commentId));

        CommentLike commentLike = commentLikeRepository.findByComment_CommentIdAndMemberId(commentId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("Like not found for the given comment and member"));

        commentLikeRepository.delete(commentLike);
        log.info("Comment unliked: commentId={}, memberId={}", commentId, memberId);
    }

    /**
     * 특정 피드의 부모 댓글(대댓글 제외) 및 그 대댓글들을 조회한다.
     *
     * @param feedId 피드 ID
     * @return 부모 댓글 및 대댓글이 포함된 CommentResponseDto 리스트
     */
    public List<CommentResponseDto> getCommentsByFeedIdWithReplies(Integer feedId) {
        // 피드 유효성 검증
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new EntityNotFoundException("Feed not found with ID: " + feedId));

        // 부모 댓글만 조회하고 각 댓글을 DTO로 변환 (대댓글 포함)
        return feedCommentRepository.findByFeedAndParentCommentIsNull(feed)
                .stream()
                .map(comment -> {
                    String profilePictureUrl = memberService.getMemberProfile(comment.getMemberId()).getProfilePictureUrl();
                    String memberName = memberService.getMemberProfile(comment.getMemberId()).getName();
                    return new CommentResponseDto(comment, profilePictureUrl, true, memberName,
                            commentLikeRepository.countByComment_CommentId(comment.getCommentId()));
                })
                .collect(Collectors.toList());
    }
}
