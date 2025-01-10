package com.capstone.project.feed.controller;

import com.capstone.project.feed.dto.request.CommentRequestDto;
import com.capstone.project.feed.dto.request.FeedRequestDto;
import com.capstone.project.feed.dto.request.RepostRequestDto;
import com.capstone.project.feed.dto.response.CommentResponseDto;
import com.capstone.project.feed.dto.response.FeedResponseDto;
import com.capstone.project.feed.service.CommentService;
import com.capstone.project.feed.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 피드 및 댓글 컨트롤러
@RestController
@RequestMapping("/api/feeds")
@RequiredArgsConstructor
@Slf4j
public class FeedController {
    private final FeedService feedService; // 피드 관련 서비스
    private final CommentService commentService; // 댓글 관련 서비스

    // 리포스트 생성
    @PostMapping("/{originalFeedId}/repost")
    public ResponseEntity<FeedResponseDto> repostFeed(
            @PathVariable Integer originalFeedId, // 원본 피드 ID
            @RequestParam Integer reposterId,     // 리포스터 ID
            @RequestBody RepostRequestDto requestDto // 리포스트 요청 데이터
    ) {
        log.info("Reposting feed with ID={} by reposterId={}", originalFeedId, reposterId);
        return ResponseEntity.ok(feedService.repostFeed(originalFeedId, reposterId, requestDto));
    }

    // 피드 생성
    @PostMapping
    public ResponseEntity<FeedResponseDto> createFeed(@RequestBody FeedRequestDto requestDto) {
        log.info("Creating feed: {}", requestDto.getContent());
        return ResponseEntity.ok(feedService.createFeed(requestDto));
    }

    // 피드 수정
    @PutMapping("/{feedId}")
    public ResponseEntity<FeedResponseDto> editFeed(
            @PathVariable Integer feedId, // 수정할 피드 ID
            @RequestBody FeedRequestDto requestDto // 수정할 내용
    ) {
        log.info("Editing feed with ID: {}", feedId);
        return ResponseEntity.ok(feedService.editFeed(feedId, requestDto));
    }

    // 피드 삭제
    @DeleteMapping("/{feedId}")
    public ResponseEntity<Void> deleteFeed(@PathVariable Integer feedId) {
        log.info("Deleting feed with ID: {}", feedId);
        feedService.deleteFeed(feedId);
        return ResponseEntity.noContent().build();
    }

    // 전체 피드 조회
    @GetMapping
    public ResponseEntity<List<FeedResponseDto>> getAllFeeds() {
        log.info("Fetching all feeds");
        return ResponseEntity.ok(feedService.getAllFeeds());
    }

    // 특정 피드 조회
    @GetMapping("/{feedId}")
    public ResponseEntity<FeedResponseDto> getFeedById(@PathVariable Integer feedId) {
        log.info("Fetching feed with ID: {}", feedId);
        return ResponseEntity.ok(feedService.getFeedById(feedId));
    }

    // 특정 피드의 댓글 조회 (대댓글 제외)
    @GetMapping("/{feedId}/comments")
    public ResponseEntity<List<CommentResponseDto>> getCommentsByFeedId(@PathVariable Integer feedId) {
        log.info("Fetching comments for feed with ID: {}", feedId);
        return ResponseEntity.ok(commentService.getCommentsByFeedId(feedId));
    }

    // 특정 피드의 댓글 및 대댓글 조회
    @GetMapping("/{feedId}/comments-with-replies")
    public ResponseEntity<List<CommentResponseDto>> getCommentsWithRepliesByFeedId(@PathVariable Integer feedId) {
        log.info("Fetching comments with replies for feed with ID: {}", feedId);
        return ResponseEntity.ok(commentService.getCommentsByFeedIdWithReplies(feedId));
    }

    // 댓글 추가 또는 대댓글 추가
    @PostMapping("/{feedId}/comments")
    public ResponseEntity<CommentResponseDto> addComment(
            @PathVariable Integer feedId, // 댓글을 추가할 피드 ID
            @RequestParam(required = false) Integer parentCommentId, // 부모 댓글 ID (optional)
            @RequestBody CommentRequestDto requestDto // 댓글 내용
    ) {
        log.info("Adding comment to feedId={}, parentCommentId={}", feedId, parentCommentId);
        return ResponseEntity.ok(commentService.addComment(feedId, requestDto, parentCommentId));
    }

    // 댓글 수정
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> editComment(
            @PathVariable Integer commentId, // 수정할 댓글 ID
            @RequestBody CommentRequestDto requestDto // 수정할 내용
    ) {
        log.info("Editing comment with ID: {}", commentId);
        return ResponseEntity.ok(commentService.editComment(commentId, requestDto));
    }

    // 댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer commentId) {
        log.info("Deleting comment with ID: {}", commentId);
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    // 특정 댓글 조회
    @GetMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> getCommentById(@PathVariable Integer commentId) {
        log.info("Fetching comment with ID: {}", commentId);
        // 댓글과 좋아요 수를 포함하여 반환
        return ResponseEntity.ok(commentService.getCommentWithLikes(commentId));
    }

    // 특정 회원의 모든 댓글 조회 (대댓글 포함)
    @GetMapping("/comments/member/{memberId}")
    public ResponseEntity<List<CommentResponseDto>> getCommentsByMemberId(@PathVariable Integer memberId) {
        log.info("Fetching comments by member with ID: {}", memberId);
        return ResponseEntity.ok(commentService.getCommentsByMemberId(memberId));
    }

    // 댓글 좋아요
    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<Void> likeComment(
            @PathVariable Integer commentId, // 좋아요를 추가할 댓글 ID
            @RequestParam Integer memberId // 좋아요를 누른 회원 ID
    ) {
        log.info("Liking commentId={} by memberId={}", commentId, memberId);
        commentService.likeComment(commentId, memberId);
        return ResponseEntity.ok().build();
    }

    // 댓글 좋아요 취소
    @DeleteMapping("/comments/{commentId}/like")
    public ResponseEntity<Void> unlikeComment(
            @PathVariable Integer commentId, // 좋아요를 취소할 댓글 ID
            @RequestParam Integer memberId // 좋아요를 취소한 회원 ID
    ) {
        log.info("Unliking commentId={} by memberId={}", commentId, memberId);
        commentService.unlikeComment(commentId, memberId);
        return ResponseEntity.ok().build();
    }

    // 저장된 게시물 조회
    @GetMapping("/members/{memberId}/saved-posts")
    public ResponseEntity<List<FeedResponseDto>> getSavedPostsByMemberId(@PathVariable Integer memberId) {
        log.info("Fetching saved posts for memberId={}", memberId);
        return ResponseEntity.ok(feedService.getSavedPostsByMemberId(memberId));
    }

    // 피드 좋아요 추가
    @PostMapping("/{feedId}/like")
    public ResponseEntity<Void> likeFeed(
            @PathVariable Integer feedId, // 좋아요를 추가할 피드 ID
            @RequestParam Integer memberId // 좋아요를 누른 회원 ID
    ) {
        log.info("Liking feedId={} by memberId={}", feedId, memberId);
        feedService.likeFeed(feedId, memberId);
        return ResponseEntity.ok().build();
    }

    // 피드 좋아요 취소
    @DeleteMapping("/{feedId}/like")
    public ResponseEntity<Void> unlikeFeed(
            @PathVariable Integer feedId, // 좋아요를 취소할 피드 ID
            @RequestParam Integer memberId // 좋아요를 취소한 회원 ID
    ) {
        log.info("Unliking feedId={} by memberId={}", feedId, memberId);
        feedService.unlikeFeed(feedId, memberId);
        return ResponseEntity.ok().build();
    }
}
