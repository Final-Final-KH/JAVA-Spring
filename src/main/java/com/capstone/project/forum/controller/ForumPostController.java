package com.capstone.project.forum.controller;

import com.capstone.project.forum.dto.request.ForumPostRequestDto;
import com.capstone.project.forum.dto.response.ForumPostResponseDto;
import com.capstone.project.forum.dto.response.PaginationDto;
import com.capstone.project.forum.service.ForumPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forums/posts")
@RequiredArgsConstructor
public class ForumPostController {
    private final ForumPostService postService;

    // 특정 카테고리의 게시글 가져오기 (페이지네이션)
    @GetMapping
    public ResponseEntity<PaginationDto<ForumPostResponseDto>> getPostsByCategory(
            @RequestParam Integer categoryId,
            @RequestParam int page,
            @RequestParam int size
    ) {
        return ResponseEntity.ok(postService.getPostsByCategory(categoryId, page, size));
    }

    // 게시글 생성
    @PostMapping
    public ResponseEntity<ForumPostResponseDto> createPost(@RequestBody ForumPostRequestDto requestDto) {
        return ResponseEntity.ok(postService.createPost(requestDto));
    }

    // 특정 게시글 조회
    @GetMapping("/{id}")
    public ResponseEntity<ForumPostResponseDto> getPostDetails(@PathVariable Integer id) {
        return postService.getPostDetails(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 게시글 조회수 증가
    @PostMapping("/{id}/increment-view")
    public ResponseEntity<Void> incrementViewCount(@PathVariable Integer id) {
        postService.incrementViewCount(id);
        return ResponseEntity.ok().build();
    }
}
