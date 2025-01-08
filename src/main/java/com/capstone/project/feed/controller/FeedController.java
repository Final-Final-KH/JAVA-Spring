package com.capstone.project.feed.controller;

import com.capstone.project.feed.dto.request.CommentRequestDto;
import com.capstone.project.feed.dto.request.FeedRequestDto;
import com.capstone.project.feed.dto.response.CommentResponseDto;
import com.capstone.project.feed.dto.response.FeedResponseDto;
import com.capstone.project.feed.service.CommentService;
import com.capstone.project.feed.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 피드 및 댓글 컨트롤러
@RestController
@RequestMapping("/api/feeds")
@RequiredArgsConstructor
public class FeedController {
    private final FeedService feedService;
    private final CommentService commentService;

    // 피드 생성
    @PostMapping
    public ResponseEntity<FeedResponseDto> createFeed(@RequestBody FeedRequestDto requestDto) {
        return ResponseEntity.ok(feedService.createFeed(requestDto));
    }

    // 피드 수정
    @PutMapping("/{feedId}")
    public ResponseEntity<FeedResponseDto> editFeed(
            @PathVariable Integer feedId,
            @RequestBody FeedRequestDto requestDto
    ) {
        return ResponseEntity.ok(feedService.editFeed(feedId, requestDto));
    }

    // 피드 삭제
    @DeleteMapping("/{feedId}")
    public ResponseEntity<Void> deleteFeed(@PathVariable Integer feedId) {
        feedService.deleteFeed(feedId);
        return ResponseEntity.noContent().build();
    }

    // 전체 피드 조회
    @GetMapping
    public ResponseEntity<List<FeedResponseDto>> getAllFeeds() {
        return ResponseEntity.ok(feedService.getAllFeeds());
    }

    // 특정 피드 조회
    @GetMapping("/{feedId}")
    public ResponseEntity<FeedResponseDto> getFeedById(@PathVariable Integer feedId) {
        return ResponseEntity.ok(feedService.getFeedById(feedId));
    }

    // 특정 피드의 댓글 조회
    @GetMapping("/{feedId}/comments")
    public ResponseEntity<List<CommentResponseDto>> getCommentsByFeedId(@PathVariable Integer feedId) {
        return ResponseEntity.ok(commentService.getCommentsByFeedId(feedId));
    }

    // 댓글 추가
    @PostMapping("/{feedId}/comments")
    public ResponseEntity<CommentResponseDto> addComment(
            @PathVariable Integer feedId,
            @RequestBody CommentRequestDto requestDto
    ) {
        return ResponseEntity.ok(commentService.addComment(feedId, requestDto));
    }

    // 댓글 수정
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> editComment(
            @PathVariable Integer commentId,
            @RequestBody CommentRequestDto requestDto
    ) {
        return ResponseEntity.ok(commentService.editComment(commentId, requestDto));
    }

    // 댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    // 특정 댓글 조회
    @GetMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> getCommentById(@PathVariable Integer commentId) {
        return ResponseEntity.ok(commentService.getCommentById(commentId));
    }

    // 특정 회원의 모든 댓글 조회
    @GetMapping("/comments/member/{memberId}")
    public ResponseEntity<List<CommentResponseDto>> getCommentsByMemberId(@PathVariable Integer memberId) {
        return ResponseEntity.ok(commentService.getCommentsByMemberId(memberId));
    }
}
