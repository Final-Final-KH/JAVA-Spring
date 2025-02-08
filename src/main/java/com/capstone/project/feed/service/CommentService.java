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
 * 댓글 서비스 클래스.
 * 댓글 및 대댓글의 추가, 수정, 삭제, 조회 및 좋아요 처리를 담당한다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final FeedRepository feedRepository;
    private final FeedCommentRepository feedCommentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final MemberService memberService;

    /**
     * 댓글 또는 대댓글을 추가한다.
     *
     * @param feedId          댓글이 추가될 피드 ID
     * @param requestDto      댓글 요청 DTO (댓글 내용 및 작성자 ID 포함)
     * @param parentCommentId 부모 댓글 ID (대댓글일 경우; 없으면 null)
     * @return 생성된 댓글의 CommentResponseDto 반환 (기본 liked=false)
     */
    public CommentResponseDto addComment(Integer feedId, CommentRequestDto requestDto, Integer parentCommentId) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new EntityNotFoundException("Feed not found with ID: " + feedId));

        FeedComment parentComment = null;
        if (parentCommentId != null) {
            parentComment = feedCommentRepository.findById(parentCommentId)
                    .orElseThrow(() -> new EntityNotFoundException("Parent comment not found with ID: " + parentCommentId));
        }

        FeedComment comment = FeedComment.builder()
                .feed(feed)
                .memberId(requestDto.getMemberId())
                .comment(requestDto.getComment())
                .parentComment(parentComment)
                .build();

        feedCommentRepository.save(comment);
        log.info("Comment added successfully: {}", comment);

        String profilePictureUrl = memberService.getMemberProfile(comment.getMemberId()).getProfilePictureUrl();
        String memberName = memberService.getMemberProfile(comment.getMemberId()).getName();

        // currentMemberId not available here, so default liked to false.
        return new CommentResponseDto(comment, profilePictureUrl, memberName,
                commentLikeRepository.countByComment_CommentId(comment.getCommentId()), false);
    }

    /**
     * 댓글을 수정한다.
     *
     * @param commentId  수정할 댓글 ID
     * @param requestDto 수정 요청 DTO (새 댓글 내용)
     * @return 수정된 댓글의 CommentResponseDto 반환 (liked는 기본 false)
     */
    public CommentResponseDto editComment(Integer commentId, CommentRequestDto requestDto) {
        FeedComment comment = feedCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with ID: " + commentId));

        comment.setComment(requestDto.getComment());
        feedCommentRepository.save(comment);
        log.info("Comment edited successfully: {}", comment);

        String profilePictureUrl = memberService.getMemberProfile(comment.getMemberId()).getProfilePictureUrl();
        String memberName = memberService.getMemberProfile(comment.getMemberId()).getName();

        return new CommentResponseDto(comment, profilePictureUrl, memberName,
                commentLikeRepository.countByComment_CommentId(comment.getCommentId()), false);
    }

    /**
     * 댓글을 삭제한다.
     *
     * @param commentId 삭제할 댓글 ID
     */
    public void deleteComment(Integer commentId) {
        commentLikeRepository.deleteByComment_CommentId(commentId);
        FeedComment comment = feedCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with ID: " + commentId));
        feedCommentRepository.delete(comment);
        log.info("Comment deleted successfully: {}", commentId);
    }

    /**
     * 특정 피드의 부모 댓글들을 조회한다.
     *
     * @param feedId 피드 ID
     * @return 부모 댓글들의 CommentResponseDto 리스트 반환 (liked 기본 false)
     */
    public List<CommentResponseDto> getCommentsByFeedId(Integer feedId) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new EntityNotFoundException("Feed not found with ID: " + feedId));

        return feedCommentRepository.findByFeedAndParentCommentIsNull(feed)
                .stream()
                .map(comment -> {
                    String profilePictureUrl = memberService.getMemberProfile(comment.getMemberId()).getProfilePictureUrl();
                    String memberName = memberService.getMemberProfile(comment.getMemberId()).getName();
                    return new CommentResponseDto(comment, profilePictureUrl, memberName,
                            commentLikeRepository.countByComment_CommentId(comment.getCommentId()), false);
                })
                .collect(Collectors.toList());
    }

    /**
     * 특정 댓글을 조회한다.
     *
     * @param commentId 댓글 ID
     * @return 해당 댓글의 CommentResponseDto 반환 (liked 기본 false)
     */
    public CommentResponseDto getCommentWithLikes(Integer commentId) {
        FeedComment comment = feedCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with ID: " + commentId));

        String profilePictureUrl = memberService.getMemberProfile(comment.getMemberId()).getProfilePictureUrl();
        String memberName = memberService.getMemberProfile(comment.getMemberId()).getName();
        Long likesCount = commentLikeRepository.countByComment_CommentId(commentId);
        return new CommentResponseDto(comment, profilePictureUrl, memberName, likesCount, false);
    }

    /**
     * 특정 회원의 모든 댓글(대댓글 포함)을 조회하고 중복 제거한다.
     *
     * @param memberId 회원 ID
     * @return 중복 제거된 CommentResponseDto 리스트 반환 (liked 기본 false)
     */
    public List<CommentResponseDto> getCommentsByMemberId(Integer memberId) {
        List<CommentResponseDto> allComments = feedCommentRepository.findByMemberId(memberId).stream()
                .map(comment -> {
                    String profilePictureUrl = memberService.getMemberProfile(comment.getMemberId()).getProfilePictureUrl();
                    String memberName = memberService.getMemberProfile(comment.getMemberId()).getName();
                    return new CommentResponseDto(comment, profilePictureUrl, true, memberName,
                            commentLikeRepository.countByComment_CommentId(comment.getCommentId()), false);
                })
                .collect(Collectors.toList());

        Set<Integer> replyIds = new HashSet<>();
        allComments.forEach(comment -> collectReplyIds(comment, replyIds));

        return allComments.stream()
                .filter(comment -> !replyIds.contains(comment.getCommentId()))
                .collect(Collectors.toList());
    }

    /**
     * 재귀적으로 대댓글의 ID를 수집한다.
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
     * @param memberId  좋아요 취소한 회원 ID
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
     * 특정 피드의 부모 댓글 및 그 대댓글들을 조회한다.
     *
     * @param feedId 피드 ID
     * @return 부모 댓글 및 대댓글이 포함된 CommentResponseDto 리스트 반환 (liked 기본 false)
     */
    public List<CommentResponseDto> getCommentsByFeedIdWithReplies(Integer feedId) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new EntityNotFoundException("Feed not found with ID: " + feedId));

        return feedCommentRepository.findByFeedAndParentCommentIsNull(feed)
                .stream()
                .map(comment -> {
                    String profilePictureUrl = memberService.getMemberProfile(comment.getMemberId()).getProfilePictureUrl();
                    String memberName = memberService.getMemberProfile(comment.getMemberId()).getName();
                    return new CommentResponseDto(comment, profilePictureUrl, true, memberName,
                            commentLikeRepository.countByComment_CommentId(comment.getCommentId()), false);
                })
                .collect(Collectors.toList());
    }
}
