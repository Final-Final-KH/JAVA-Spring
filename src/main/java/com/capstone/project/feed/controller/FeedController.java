package com.capstone.project.feed.controller;


import com.capstone.project.feed.dto.request.FeedEditRequestDto;
import com.capstone.project.feed.dto.response.FeedResponseDto;
import com.capstone.project.feed.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feeds")
@RequiredArgsConstructor

public class FeedController {

    private final FeedService feedService;

    // Edit a post / 게시물 수정
    @PutMapping("/{feedId}")
    public ResponseEntity<FeedResponseDto> editPost(
            @PathVariable Integer feedId,
            @RequestBody FeedEditRequestDto requestDto) {
        return ResponseEntity.ok(feedService.editPost(feedId, requestDto.getMemberId(), requestDto.getNewContent()));
    }

    // Delete a post / 게시물 삭제
    @DeleteMapping("/{feedId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Integer feedId,
            @RequestParam Integer memberId) {
        feedService.deletePost(feedId, memberId);
        return ResponseEntity.noContent().build();
    }

    // Save a post / 게시물 저장
    @PostMapping("/{feedId}/save")
    public ResponseEntity<Void> savePost(
            @PathVariable Integer feedId,
            @RequestParam Integer memberId) {
        feedService.savePost(memberId, feedId);
        return ResponseEntity.ok().build();
    }
}
