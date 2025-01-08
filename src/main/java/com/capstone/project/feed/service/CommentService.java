package com.capstone.project.feed.service;

import com.capstone.project.feed.dto.request.CommentRequestDto;
import com.capstone.project.feed.dto.response.CommentResponseDto;
import com.capstone.project.feed.entity.Feed;
import com.capstone.project.feed.entity.FeedComment;
import com.capstone.project.feed.repository.FeedCommentRepository;
import com.capstone.project.feed.repository.FeedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

// 댓글 서비스
@Service
@RequiredArgsConstructor
public class CommentService {
    private final FeedRepository feedRepository;
    private final FeedCommentRepository feedCommentRepository;

    // 댓글 추가
    public CommentResponseDto addComment(Integer feedId, CommentRequestDto requestDto) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new EntityNotFoundException("Feed not found with ID: " + feedId));

        // FeedComment 객체 생성 및 저장
        FeedComment comment = FeedComment.builder()
                .feed(feed)
                .memberId(requestDto.getMemberId())
                .comment(requestDto.getComment())
                .build();

        feedCommentRepository.save(comment);
        return new CommentResponseDto(comment);
    }

    // 댓글 수정
    public CommentResponseDto editComment(Integer commentId, CommentRequestDto requestDto) {
        FeedComment comment = feedCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with ID: " + commentId));

        comment.setComment(requestDto.getComment()); // 댓글 내용 수정
        feedCommentRepository.save(comment); // 수정된 댓글 저장
        return new CommentResponseDto(comment);
    }

    // 댓글 삭제
    public void deleteComment(Integer commentId) {
        FeedComment comment = feedCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with ID: " + commentId));
        feedCommentRepository.delete(comment); // 댓글 삭제
    }

    // 특정 피드의 댓글 조회
    public List<CommentResponseDto> getCommentsByFeedId(Integer feedId) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new EntityNotFoundException("Feed not found with ID: " + feedId));

        // Feed에 연결된 모든 댓글 조회
        return feed.getComments().stream()
                .map(CommentResponseDto::new)
                .collect(Collectors.toList());
    }

    // 특정 댓글 조회
    public CommentResponseDto getCommentById(Integer commentId) {
        FeedComment comment = feedCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with ID: " + commentId));
        return new CommentResponseDto(comment); // 댓글 정보를 CommentResponseDto로 반환
    }

    // 특정 회원의 모든 댓글 조회
    public List<CommentResponseDto> getCommentsByMemberId(Integer memberId) {
        return feedCommentRepository.findByMemberId(memberId)
                .stream()
                .map(CommentResponseDto::new)
                .collect(Collectors.toList());
    }
}
