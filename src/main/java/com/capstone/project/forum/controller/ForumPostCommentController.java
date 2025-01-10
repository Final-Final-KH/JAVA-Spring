package com.capstone.project.forum.controller;

import com.capstone.project.forum.dto.request.ForumPostCommentRequestDto;
import com.capstone.project.forum.dto.response.ForumPostCommentResponseDto;
import com.capstone.project.forum.service.ForumPostCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forums/comments")
@RequiredArgsConstructor
public class ForumPostCommentController {
    private final ForumPostCommentService commentService;

    // 특정 게시글의 댓글 가져오기
    @GetMapping
    public ResponseEntity<List<ForumPostCommentResponseDto>> getCommentsForPost(@RequestParam Integer postId) {
        return ResponseEntity.ok(commentService.getCommentsForPost(postId));
    }

    // 댓글 추가
    @PostMapping
    public ResponseEntity<ForumPostCommentResponseDto> createComment(@RequestBody ForumPostCommentRequestDto requestDto) {
        return ResponseEntity.ok(commentService.createComment(requestDto));
    }

    // 댓글에 대한 답글 추가 (인용)
    @PostMapping("/{commentId}/reply")
    public ResponseEntity<ForumPostCommentResponseDto> replyToComment(
            @PathVariable Integer commentId,
            @RequestBody ForumPostCommentRequestDto requestDto
    ) {
        return ResponseEntity.ok(commentService.replyToComment(commentId, requestDto));
    }

    // 게시글(OP)에 대한 답글 추가 (인용)
    @PostMapping("/post/{postId}/reply")
    public ResponseEntity<ForumPostCommentResponseDto> replyToPost(
            @PathVariable Integer postId,
            @RequestBody ForumPostCommentRequestDto requestDto
    ) {
        return ResponseEntity.ok(commentService.replyToPost(postId, requestDto));
    }
}
