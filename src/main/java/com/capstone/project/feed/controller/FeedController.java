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

/**
 * 피드 및 댓글 관련 컨트롤러.
 * 클라이언트의 요청에 따라 피드 생성, 수정, 삭제, 댓글 추가, 수정, 삭제, 좋아요 처리 등을 담당한다.
 */
@RestController
@RequestMapping("/api/feeds")
@RequiredArgsConstructor
@Slf4j
public class FeedController {
    private final FeedService feedService;
    private final CommentService commentService;

    /**
     * 리포스트 생성 엔드포인트.
     *
     * @param originalFeedId 원본 피드 ID
     * @param reposterId     리포스터 ID
     * @param requestDto     리포스트 요청 데이터
     * @return 생성된 리포스트 FeedResponseDto 반환
     */
    @PostMapping("/{originalFeedId}/repost")
    public ResponseEntity<FeedResponseDto> repostFeed(
            @PathVariable Integer originalFeedId,
            @RequestParam Integer reposterId,
            @RequestBody RepostRequestDto requestDto
    ) {
        log.info("Reposting feed with ID={} by reposterId={}", originalFeedId, reposterId);
        return ResponseEntity.ok(feedService.repostFeed(originalFeedId, reposterId, requestDto));
    }

    /**
     * 피드 생성 엔드포인트.
     *
     * @param requestDto 피드 생성 요청 DTO
     * @return 생성된 피드 FeedResponseDto 반환
     */
    @PostMapping
    public ResponseEntity<FeedResponseDto> createFeed(@RequestBody FeedRequestDto requestDto) {
        log.info("Creating feed: {}", requestDto.getContent());
        return ResponseEntity.ok(feedService.createFeed(requestDto));
    }

    /**
     * 피드 수정 엔드포인트.
     *
     * @param feedId     수정할 피드 ID
     * @param requestDto 수정 요청 DTO
     * @return 수정된 피드 FeedResponseDto 반환
     */
    @PutMapping("/{feedId}")
    public ResponseEntity<FeedResponseDto> editFeed(
            @PathVariable Integer feedId,
            @RequestBody FeedRequestDto requestDto
    ) {
        log.info("Editing feed with ID: {}", feedId);
        return ResponseEntity.ok(feedService.editFeed(feedId, requestDto));
    }

    /**
     * 피드 삭제 엔드포인트.
     *
     * @param feedId 삭제할 피드 ID
     * @return 상태 코드 204(No Content) 반환
     */
    @DeleteMapping("/{feedId}")
    public ResponseEntity<Void> deleteFeed(@PathVariable Integer feedId) {
        log.info("Deleting feed with ID: {}", feedId);
        feedService.deleteFeed(feedId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 전체 피드 조회 엔드포인트.
     * 클라이언트에서 현재 사용자 ID(memberId)를 쿼리 파라미터로 전달하여 좋아요 여부(liked)를 포함시킨다.
     *
     * @param memberId 현재 사용자 ID (optional)
     * @return 모든 피드 FeedResponseDto 리스트 반환
     */
    @GetMapping
    public ResponseEntity<List<FeedResponseDto>> getAllFeeds(@RequestParam(required = false) Integer memberId) {
        log.info("Fetching all feeds for memberId={}", memberId);
        return ResponseEntity.ok(feedService.getAllFeeds(memberId));
    }

    /**
     * 특정 피드 조회 엔드포인트.
     * 현재 사용자 ID(memberId)를 쿼리 파라미터로 전달받아 좋아요 여부(liked)를 설정한다.
     *
     * @param feedId   피드 ID
     * @param memberId 현재 사용자 ID
     * @return 해당 피드 FeedResponseDto 반환
     */
    @GetMapping("/{feedId}")
    public ResponseEntity<FeedResponseDto> getFeedById(@PathVariable Integer feedId,
                                                       @RequestParam(required = false) Integer memberId) {
        log.info("Fetching feed with ID: {} for memberId={}", feedId, memberId);
        return ResponseEntity.ok(feedService.getFeedById(feedId, memberId));
    }

    /**
     * 특정 피드의 댓글 조회 엔드포인트 (대댓글 제외).
     *
     * @param feedId 피드 ID
     * @return 댓글 리스트 (CommentResponseDto) 반환
     */
    @GetMapping("/{feedId}/comments")
    public ResponseEntity<List<CommentResponseDto>> getCommentsByFeedId(@PathVariable Integer feedId) {
        log.info("Fetching comments for feed with ID: {}", feedId);
        return ResponseEntity.ok(commentService.getCommentsByFeedId(feedId));
    }

    /**
     * 특정 피드의 댓글 및 대댓글 조회 엔드포인트.
     *
     * @param feedId 피드 ID
     * @return 댓글 및 대댓글 리스트 (CommentResponseDto) 반환
     */
    @GetMapping("/{feedId}/comments-with-replies")
    public ResponseEntity<List<CommentResponseDto>> getCommentsWithRepliesByFeedId(@PathVariable Integer feedId) {
        log.info("Fetching comments with replies for feed with ID: {}", feedId);
        return ResponseEntity.ok(commentService.getCommentsByFeedIdWithReplies(feedId));
    }

    /**
     * 댓글 또는 대댓글 추가 엔드포인트.
     *
     * @param feedId          댓글을 추가할 피드 ID
     * @param parentCommentId 부모 댓글 ID (optional)
     * @param requestDto      댓글 요청 DTO
     * @return 생성된 댓글의 CommentResponseDto 반환
     */
    @PostMapping("/{feedId}/comments")
    public ResponseEntity<CommentResponseDto> addComment(
            @PathVariable Integer feedId,
            @RequestParam(required = false) Integer parentCommentId,
            @RequestBody CommentRequestDto requestDto
    ) {
        log.info("Adding comment to feedId={}, parentCommentId={}", feedId, parentCommentId);
        return ResponseEntity.ok(commentService.addComment(feedId, requestDto, parentCommentId));
    }

    /**
     * 댓글 수정 엔드포인트.
     *
     * @param commentId  수정할 댓글 ID
     * @param requestDto 수정 요청 DTO
     * @return 수정된 댓글의 CommentResponseDto 반환
     */
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> editComment(
            @PathVariable Integer commentId,
            @RequestBody CommentRequestDto requestDto
    ) {
        log.info("Editing comment with ID: {}", commentId);
        return ResponseEntity.ok(commentService.editComment(commentId, requestDto));
    }

    /**
     * 댓글 삭제 엔드포인트.
     *
     * @param commentId 삭제할 댓글 ID
     * @return 상태 코드 204(No Content) 반환
     */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer commentId) {
        log.info("Deleting comment with ID: {}", commentId);
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 특정 댓글 조회 엔드포인트.
     *
     * @param commentId 댓글 ID
     * @return 해당 댓글의 CommentResponseDto 반환
     */
    @GetMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> getCommentById(@PathVariable Integer commentId) {
        log.info("Fetching comment with ID: {}", commentId);
        return ResponseEntity.ok(commentService.getCommentWithLikes(commentId));
    }

    /**
     * 특정 회원의 모든 댓글 조회 엔드포인트.
     *
     * @param memberId 회원 ID
     * @return 해당 회원의 댓글 리스트 (CommentResponseDto) 반환
     */
    @GetMapping("/comments/member/{memberId}")
    public ResponseEntity<List<CommentResponseDto>> getCommentsByMemberId(@PathVariable Integer memberId) {
        log.info("Fetching comments by member with ID: {}", memberId);
        return ResponseEntity.ok(commentService.getCommentsByMemberId(memberId));
    }

    /**
     * 댓글 좋아요 추가 엔드포인트.
     *
     * @param commentId 댓글 ID
     * @param memberId  좋아요를 누른 회원 ID
     * @return 상태 코드 200(OK) 반환
     */
    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<Void> likeComment(
            @PathVariable Integer commentId,
            @RequestParam Integer memberId
    ) {
        log.info("Liking commentId={} by memberId={}", commentId, memberId);
        commentService.likeComment(commentId, memberId);
        return ResponseEntity.ok().build();
    }

    /**
     * 댓글 좋아요 취소 엔드포인트.
     *
     * @param commentId 댓글 ID
     * @param memberId  좋아요를 취소한 회원 ID
     * @return 상태 코드 200(OK) 반환
     */
    @DeleteMapping("/comments/{commentId}/like")
    public ResponseEntity<Void> unlikeComment(
            @PathVariable Integer commentId,
            @RequestParam Integer memberId
    ) {
        log.info("Unliking commentId={} by memberId={}", commentId, memberId);
        commentService.unlikeComment(commentId, memberId);
        return ResponseEntity.ok().build();
    }

    /**
     * 저장된 게시물 조회 엔드포인트.
     *
     * @param memberId 회원 ID
     * @return 저장된 게시물 리스트 (FeedResponseDto) 반환
     */
    @GetMapping("/members/{memberId}/saved-posts")
    public ResponseEntity<List<FeedResponseDto>> getSavedPostsByMemberId(@PathVariable Integer memberId) {
        log.info("Fetching saved posts for memberId={}", memberId);
        return ResponseEntity.ok(feedService.getSavedPostsByMemberId(memberId));
    }

    /**
     * 피드 좋아요 추가 엔드포인트.
     *
     * @param feedId   피드 ID
     * @param memberId 좋아요를 누른 회원 ID
     * @return 상태 코드 200(OK) 반환
     */
    @PostMapping("/{feedId}/like")
    public ResponseEntity<Void> likeFeed(
            @PathVariable Integer feedId,
            @RequestParam Integer memberId
    ) {
        log.info("Liking feedId={} by memberId={}", feedId, memberId);
        feedService.likeFeed(feedId, memberId);
        return ResponseEntity.ok().build();
    }

    /**
     * 피드 좋아요 취소 엔드포인트.
     *
     * @param feedId   피드 ID
     * @param memberId 좋아요를 취소한 회원 ID
     * @return 상태 코드 200(OK) 반환
     */
    @DeleteMapping("/{feedId}/like")
    public ResponseEntity<Void> unlikeFeed(
            @PathVariable Integer feedId,
            @RequestParam Integer memberId
    ) {
        log.info("Unliking feedId={} by memberId={}", feedId, memberId);
        feedService.unlikeFeed(feedId, memberId);
        return ResponseEntity.ok().build();
    }
}
