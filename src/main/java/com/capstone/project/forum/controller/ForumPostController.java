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
     * @param page 페이지 번호 (1부터 시작)
     * @param size 페이지 크기
     * @return 게시글 목록 (페이지네이션 포함)
     */
    @GetMapping
    public ResponseEntity<PaginationDto<ForumPostResponseDto>> getPostsByCategory(
            @RequestParam Integer categoryId,
            @RequestParam int page,
            @RequestParam int size
    ) {
        // page 값을 0 기반으로 변환 (1부터 시작하는 페이지를 0 기반으로 변경)
        int zeroBasedPage = page > 0 ? page - 1 : 0;

        // 서비스 메서드 호출 (변환된 page 값 사용)
        return ResponseEntity.ok(postService.getPostsByCategory(categoryId, zeroBasedPage, size));
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
     * @param loggedInMemberId 현재 로그인된 사용자 ID
     * @param isAdmin 현재 사용자가 관리자 여부
     * @return 수정된 게시글 정보
     */
    @PutMapping("/{id}")
    public ResponseEntity<ForumPostResponseDto> editPost(
            @PathVariable Integer id,
            @RequestBody ForumPostRequestDto requestDto,
            @RequestParam Integer loggedInMemberId,
            @RequestParam boolean isAdmin
    ) {
        return ResponseEntity.ok(postService.updatePost(id, requestDto, loggedInMemberId, isAdmin)); // 수정 메서드 호출
    }

    /**
     * 게시글 삭제
     * @param id 삭제할 게시글 ID
     * @param loggedInMemberId 요청 사용자 ID
     * @param isAdmin 관리자 여부
     * @return 성공 상태
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Integer id,
            @RequestParam Integer loggedInMemberId,
            @RequestParam boolean isAdmin
    ) {
        postService.deletePost(id, loggedInMemberId, isAdmin);
        return ResponseEntity.ok().build();
    }

    /**
     * 게시글 복구
     * @param id 복구할 게시글 ID
     * @return 성공 상태
     */
    @PostMapping("/{id}/restore")
    public ResponseEntity<Void> restorePost(@PathVariable Integer id) {
        postService.undeletePost(id);
        return ResponseEntity.ok().build();
    }

    // canEditPost와 canDeletePost는 수정 및 삭제 권한을 사전에 확인하는 API
    // 이는 프론트엔드가 수정/삭제 요청 전에 해당 사용자의 권한을 확인하기 위해 사용
    // 데이터 수정 및 삭제 작업 (updatePost, deletePost)에서는 여전히 권한을 검증하지만, 이 메서드는 권한 확인 전용
    /**
     * 게시글 수정 여부 확인 (API 추가)
     *
     * @param id 게시글 ID
     * @param loggedInMemberId 요청 사용자 ID
     * @return 게시글 수정 권한이 있는지 여부
     */
    @GetMapping("/{id}/can-edit")
    public ResponseEntity<Boolean> canEditPost(
            @PathVariable Integer id,
            @RequestParam Integer loggedInMemberId
    ) {
        // Service 계층에서 수정 권한 확인
        boolean canEdit = postService.canEditPost(id, loggedInMemberId);
        return ResponseEntity.ok(canEdit);
    }

    /**
     * 게시글 삭제 여부 확인 (API 추가)
     *
     * @param id 게시글 ID
     * @param loggedInMemberId 요청 사용자 ID
     * @return 게시글 삭제 권한이 있는지 여부
     */
    @GetMapping("/{id}/can-delete")
    public ResponseEntity<Boolean> canDeletePost(
            @PathVariable Integer id,
            @RequestParam Integer loggedInMemberId
    ) {
        // Service 계층에서 삭제 권한 확인
        boolean canDelete = postService.canDeletePost(id, loggedInMemberId);
        return ResponseEntity.ok(canDelete);
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
