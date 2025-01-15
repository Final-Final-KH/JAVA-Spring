package com.capstone.project.forum.controller;

import com.capstone.project.forum.dto.request.ForumPostLikeRequestDto;
import com.capstone.project.forum.dto.response.ForumPostLikeResponseDto;
import com.capstone.project.forum.service.ForumPostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/forums/likes")
@RequiredArgsConstructor
public class ForumPostLikeController {
    private final ForumPostLikeService likeService;

    // 게시글 좋아요 토글
    @PostMapping("/post/toggle")
    public ResponseEntity<Void> togglePostLike(@RequestBody ForumPostLikeRequestDto requestDto) {
        likeService.togglePostLike(requestDto.getMemberId(), requestDto.getPostId());
        return ResponseEntity.ok().build();
    }

    // 댓글 좋아요 토글
    @PostMapping("/comment/toggle")
    public ResponseEntity<Void> toggleCommentLike(@RequestBody ForumPostLikeRequestDto requestDto) {
        likeService.toggleCommentLike(requestDto.getMemberId(), requestDto.getCommentId());
        return ResponseEntity.ok().build();
    }

    // 게시글에 좋아요 여부 확인
    @GetMapping("/post")
    public ResponseEntity<ForumPostLikeResponseDto> hasLikedPost(
            @RequestParam Integer memberId,
            @RequestParam Integer postId
    ) {
        boolean liked = likeService.hasLikedPost(memberId, postId);
        return ResponseEntity.ok(new ForumPostLikeResponseDto(liked, null)); // 총 좋아요 수는 null
    }

    // 댓글에 좋아요 여부 확인
    @GetMapping("/comment")
    public ResponseEntity<ForumPostLikeResponseDto> hasLikedComment(
            @RequestParam Integer memberId,
            @RequestParam Integer commentId
    ) {
        boolean liked = likeService.hasLikedComment(memberId, commentId);
        return ResponseEntity.ok(new ForumPostLikeResponseDto(liked, null)); // 총 좋아요 수는 null
    }
}
