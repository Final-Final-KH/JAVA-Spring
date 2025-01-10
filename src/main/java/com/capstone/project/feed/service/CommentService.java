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

// 댓글 서비스
@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final FeedRepository feedRepository; // Feed 엔티티와 상호작용하는 리포지토리
    private final FeedCommentRepository feedCommentRepository; // FeedComment 엔티티와 상호작용하는 리포지토리
    private final CommentLikeRepository commentLikeRepository; // 댓글 좋아요 관련 리포지토리
    private final MemberService memberService; // 멤버 서비스

    // 댓글 추가 또는 대댓글 추가
    public CommentResponseDto addComment(Integer feedId, CommentRequestDto requestDto, Integer parentCommentId) {
        // Feed 유효성 검증
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new EntityNotFoundException("Feed not found with ID: " + feedId));

        FeedComment parentComment = null;
        if (parentCommentId != null) {
            // 부모 댓글 유효성 검증
            parentComment = feedCommentRepository.findById(parentCommentId)
                    .orElseThrow(() -> new EntityNotFoundException("Parent comment not found with ID: " + parentCommentId));
        }

        // FeedComment 객체 생성 및 저장
        FeedComment comment = FeedComment.builder()
                .feed(feed) // 댓글이 속한 Feed 설정
                .memberId(requestDto.getMemberId()) // 작성자 ID 설정
                .comment(requestDto.getComment()) // 댓글 내용 설정
                .parentComment(parentComment) // 부모 댓글 설정 (대댓글의 경우)
                .build();

        feedCommentRepository.save(comment); // 댓글 저장
        log.info("Comment added successfully: {}", comment);

        // 작성자의 프로필 사진 URL 가져오기
        String profilePictureUrl = memberService.getMemberProfile(comment.getMemberId()).getProfilePictureUrl();

        return new CommentResponseDto(
                comment,
                profilePictureUrl,
                commentLikeRepository.countByComment_CommentId(comment.getCommentId()) // 좋아요 수 추가
        ); // 저장된 댓글 반환
    }

    // 댓글 수정
    public CommentResponseDto editComment(Integer commentId, CommentRequestDto requestDto) {
        // 댓글 유효성 검증
        FeedComment comment = feedCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with ID: " + commentId));

        comment.setComment(requestDto.getComment()); // 댓글 내용 수정
        feedCommentRepository.save(comment); // 수정된 댓글 저장
        log.info("Comment edited successfully: {}", comment);

        // 작성자의 프로필 사진 URL 가져오기
        String profilePictureUrl = memberService.getMemberProfile(comment.getMemberId()).getProfilePictureUrl();

        return new CommentResponseDto(
                comment,
                profilePictureUrl,
                commentLikeRepository.countByComment_CommentId(comment.getCommentId()) // 좋아요 수 추가
        ); // 수정된 댓글 반환
    }

    // 댓글 삭제
    public void deleteComment(Integer commentId) {
        // Delete associated likes first
        commentLikeRepository.deleteByComment_CommentId(commentId);

        // Then delete the comment
        FeedComment comment = feedCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with ID: " + commentId));
        feedCommentRepository.delete(comment);
        log.info("Comment deleted successfully: {}", commentId);
    }

    // 특정 피드의 댓글 조회 (대댓글 제외)
    public List<CommentResponseDto> getCommentsByFeedId(Integer feedId) {
        // Feed 유효성 검증
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new EntityNotFoundException("Feed not found with ID: " + feedId));

        // Feed에 연결된 모든 댓글 조회 (부모 댓글만)
        return feedCommentRepository.findByFeedAndParentCommentIsNull(feed)
                .stream()
                .map(comment -> {
                    String profilePictureUrl = memberService.getMemberProfile(comment.getMemberId()).getProfilePictureUrl();
                    return new CommentResponseDto(
                            comment,
                            profilePictureUrl,
                            commentLikeRepository.countByComment_CommentId(comment.getCommentId()) // 좋아요 수 추가
                    );
                }) // FeedComment -> CommentResponseDto 변환
                .collect(Collectors.toList());
    }

    // 특정 댓글 조회 (좋아요 수 포함)
    public CommentResponseDto getCommentWithLikes(Integer commentId) {
        // 댓글 유효성 검증
        FeedComment comment = feedCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with ID: " + commentId));

        // 작성자의 프로필 사진 URL 가져오기
        String profilePictureUrl = memberService.getMemberProfile(comment.getMemberId()).getProfilePictureUrl();

        // 좋아요 수 가져오기
        Long likesCount = commentLikeRepository.countByComment_CommentId(commentId);

        return new CommentResponseDto(comment, profilePictureUrl, likesCount); // 댓글 반환
    }

    // 특정 회원의 모든 댓글 조회 (대댓글 포함, 중복 제거)
    public List<CommentResponseDto> getCommentsByMemberId(Integer memberId) {
        // 모든 댓글 조회
        List<CommentResponseDto> allComments = feedCommentRepository.findByMemberId(memberId).stream()
                .map(comment -> {
                    String profilePictureUrl = memberService.getMemberProfile(comment.getMemberId()).getProfilePictureUrl();
                    return new CommentResponseDto(
                            comment,
                            profilePictureUrl,
                            true,
                            commentLikeRepository.countByComment_CommentId(comment.getCommentId()) // 좋아요 수 추가
                    );
                })
                .collect(Collectors.toList());

        // 중복 제거: 대댓글에 포함된 댓글을 상위 레벨에서 제거
        Set<Integer> replyIds = new HashSet<>();
        allComments.forEach(comment -> collectReplyIds(comment, replyIds));

        return allComments.stream()
                .filter(comment -> !replyIds.contains(comment.getCommentId()))
                .collect(Collectors.toList());
    }

    // 대댓글 ID를 수집하는 재귀 메서드
    private void collectReplyIds(CommentResponseDto comment, Set<Integer> replyIds) {
        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            for (CommentResponseDto reply : comment.getReplies()) {
                replyIds.add(reply.getCommentId()); // 대댓글 ID 추가
                collectReplyIds(reply, replyIds); // 재귀적으로 대댓글 처리
            }
        }
    }

    // 댓글 좋아요
    public void likeComment(Integer commentId, Integer memberId) {
        // 댓글 유효성 검증
        FeedComment comment = feedCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with ID: " + commentId));

        // 이미 좋아요를 눌렀는지 확인
        if (commentLikeRepository.existsByComment_CommentIdAndMemberId(commentId, memberId)) {
            throw new IllegalArgumentException("Comment already liked by this member");
        }

        // CommentLike 객체 생성 및 저장
        CommentLike like = CommentLike.builder()
                .comment(comment) // 댓글 설정
                .memberId(memberId) // 좋아요 누른 사용자 ID 설정
                .build();

        commentLikeRepository.save(like); // 좋아요 저장
        log.info("Comment liked: commentId={}, memberId={}", commentId, memberId);
    }

    // 댓글 좋아요 취소
    public void unlikeComment(Integer commentId, Integer memberId) {
        FeedComment comment = feedCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with ID: " + commentId));

        CommentLike commentLike = commentLikeRepository.findByComment_CommentIdAndMemberId(commentId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("Like not found for the given comment and member"));

        commentLikeRepository.delete(commentLike);
        log.info("Comment unliked: commentId={}, memberId={}", commentId, memberId);
    }

    // 특정 피드의 댓글 및 대댓글 조회
    public List<CommentResponseDto> getCommentsByFeedIdWithReplies(Integer feedId) {
        // Feed 유효성 검증
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new EntityNotFoundException("Feed not found with ID: " + feedId));

        // 부모 댓글만 반환 (parentComment가 null인 댓글)
        return feedCommentRepository.findByFeedAndParentCommentIsNull(feed).stream()
                .map(comment -> {
                    String profilePictureUrl = memberService.getMemberProfile(comment.getMemberId()).getProfilePictureUrl();
                    return new CommentResponseDto(
                            comment,
                            profilePictureUrl,
                            true,
                            commentLikeRepository.countByComment_CommentId(comment.getCommentId()) // 좋아요 수 추가
                    );
                }) // 대댓글 포함
                .collect(Collectors.toList());
    }
}
