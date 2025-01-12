package com.capstone.project.forum.controller;

import com.capstone.project.forum.dto.request.ForumPostRequestDto;
import com.capstone.project.forum.dto.response.ForumPostResponseDto;
import com.capstone.project.forum.dto.response.PaginationDto;
import com.capstone.project.forum.service.ForumPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 게시글 컨트롤러 클래스
 * REST API 엔드포인트를 정의하고 Service 계층 호출
 */
@RestController
@RequestMapping("/api/forums/posts")
@RequiredArgsConstructor
public class ForumPostController {

    private final ForumPostService postService; // Service 계층 의존성 주입

    /**
     * 특정 카테고리의 게시글 가져오기 (페이지네이션)
     *
     * @param categoryId 카테고리 ID
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 게시글 목록 (페이지네이션 포함)
     */
    @GetMapping
    public ResponseEntity<PaginationDto<ForumPostResponseDto>> getPostsByCategory(
            @RequestParam Integer categoryId,
            @RequestParam int page,
            @RequestParam int size
    ) {
        return ResponseEntity.ok(postService.getPostsByCategory(categoryId, page, size)); // Service 메서드 호출
    }

    /**
     * 게시글 생성
     *
     * @param requestDto 게시글 생성 요청 데이터
     * @return 생성된 게시글 정보
     */
    @PostMapping
    public ResponseEntity<ForumPostResponseDto> createPost(@RequestBody ForumPostRequestDto requestDto) {
        return ResponseEntity.ok(postService.createPost(requestDto)); // 게시글 생성 로직 호출
    }

    /**
     * 게시글 수정
     *
     * @param id 수정할 게시글 ID
     * @param requestDto 수정 요청 데이터
     * @return 수정된 게시글 정보
     */
    @PutMapping("/{id}")
    public ResponseEntity<ForumPostResponseDto> editPost(
            @PathVariable Integer id,
            @RequestBody ForumPostRequestDto requestDto
    ) {
        return ResponseEntity.ok(postService.updatePost(id, requestDto)); // 수정 메서드 호출
    }

    /**
     * 특정 게시글 조회
     *
     * @param id 게시글 ID
     * @return 게시글 상세 정보 또는 404 오류
     */
    @GetMapping("/{id}")
    public ResponseEntity<ForumPostResponseDto> getPostDetails(@PathVariable Integer id) {
        return postService.getPostDetails(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()); // Optional 처리
    }

    /**
     * 게시글 조회수 증가
     *
     * @param id 게시글 ID
     * @return 성공 상태
     */
    @PostMapping("/{id}/increment-view")
    public ResponseEntity<Void> incrementViewCount(@PathVariable Integer id) {
        postService.incrementViewCount(id); // 조회수 증가 메서드 호출
        return ResponseEntity.ok().build();
    }

    /**
     * 게시글 삭제
     *
     * @param id 게시글 ID
     * @param cascadeComments 댓글 포함 삭제 여부
     * @return 성공 상태
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "false") boolean cascadeComments,
            @RequestParam(defaultValue = "OP") String removedBy // Default is "OP"
    ) {
        postService.deletePost(id, cascadeComments, removedBy);
        return ResponseEntity.ok().build();
    }


    /**
     * 게시글 인용
     *
     * @param quotingMemberId 인용하는 회원 ID
     * @param quotedPostId 인용 대상 게시글 ID
     * @param commentContent 추가 댓글 내용
     * @return 인용된 게시글 정보
     */
    @PostMapping("/{id}/quote")
    public ResponseEntity<ForumPostResponseDto> quotePost(
            @RequestParam Integer quotingMemberId,
            @PathVariable("id") Integer quotedPostId,
            @RequestBody String commentContent
    ) {
        return ResponseEntity.ok(postService.quotePost(quotingMemberId, quotedPostId, commentContent)); // 인용 메서드 호출
    }
}
