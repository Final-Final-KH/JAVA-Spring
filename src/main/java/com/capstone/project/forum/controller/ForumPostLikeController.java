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

    // 게시글에 좋아요 추가
    @PostMapping("/post")
    public ResponseEntity<Void> likePost(@RequestBody ForumPostLikeRequestDto requestDto) {
        likeService.likePost(requestDto.getMemberId(), requestDto.getPostId());
        return ResponseEntity.ok().build();
    }

    // 댓글에 좋아요 추가
    @PostMapping("/comment")
    public ResponseEntity<Void> likeComment(@RequestBody ForumPostLikeRequestDto requestDto) {
        likeService.likeComment(requestDto.getMemberId(), requestDto.getCommentId());
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
