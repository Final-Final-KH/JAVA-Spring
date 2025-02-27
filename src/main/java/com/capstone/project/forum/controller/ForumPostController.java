package com.capstone.project.forum.controller;

import com.capstone.project.forum.dto.request.ForumPostRequestDto;
import com.capstone.project.forum.dto.response.ForumPostResponseDto;
import com.capstone.project.forum.dto.response.PaginationDto;
import com.capstone.project.forum.service.FileService;
import com.capstone.project.forum.service.ForumPostService;
import com.capstone.project.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 게시글 컨트롤러 클래스
 * REST API 엔드포인트를 정의하고 Service 계층 호출
 */
@RestController
@RequestMapping("/api/forums/posts")
@RequiredArgsConstructor
@Slf4j
public class ForumPostController {

    private final ForumPostService postService; // Service 계층 의존성 주입
    private final MemberService memberService;
    private final FileService fileService;

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
     * @param requestDto 게시글 데이터 (제목, 내용, 카테고리 ID 등)
     * @return 생성된 게시글 정보
     */
    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody ForumPostRequestDto requestDto) {
        log.info("Creating post with title: {}", requestDto.getTitle());

        // 필수 필드 검증 (예외 처리)
        if (requestDto.getMemberId() == null) {
            log.warn("Member ID is missing in the request.");
            return ResponseEntity.badRequest().body("Member ID is required."); // Member ID가 필수
        }
        if (requestDto.getCategoryId() == null) {
            log.warn("Category ID is missing in the request.");
            return ResponseEntity.badRequest().body("Category ID is required."); // Category ID가 필수
        }
        if (requestDto.getTitle() == null || requestDto.getTitle().isEmpty()) {
            log.warn("Title is missing or empty.");
            return ResponseEntity.badRequest().body("Title is required."); // 제목이 필수
        }
        if (requestDto.getContent() == null || requestDto.getContent().isEmpty()) {
            log.warn("Content is missing or empty.");
            return ResponseEntity.badRequest().body("Content is required."); // 내용이 필수
        }

        try {
            // 게시글 생성 서비스 호출
            ForumPostResponseDto responseDto = postService.createPost(requestDto);

            log.info("Post created successfully with ID: {}", responseDto.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto); // 성공 시 HTTP 201 반환
        } catch (IllegalArgumentException e) {
            // 잘못된 요청 처리
            log.error("Error creating post: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage()); // 잘못된 요청 응답 반환
        } catch (Exception e) {
            // 예상치 못한 예외 처리
            log.error("Unexpected error creating post", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred."); // 서버 에러 응답
        }
    }



    /**
     * 게시글 제목 수정
     *
     * @param postId           수정할 게시글 ID
     * @param body             새로운 제목을 포함한 request body (JSON 형식)
     * @param loggedInMemberId 요청 사용자 ID
     * @return 수정된 게시글 정보
     */
    @PutMapping("/{postId}/title")
    public ResponseEntity<ForumPostResponseDto> updatePostTitle(
            @PathVariable Integer postId,
            @RequestBody Map<String, String> body,
            @RequestParam Integer loggedInMemberId
    ) {
        log.info("Updating post title for ID: {} by member ID: {}", postId, loggedInMemberId);

        boolean isAdmin = memberService.isAdmin(loggedInMemberId); // 관리자 여부 확인
        try {
            var updatedPost = postService.updatePostTitle(postId, body.get("title"), loggedInMemberId, isAdmin);
            return ResponseEntity.ok(updatedPost);
        } catch (SecurityException e) {
            log.error("Unauthorized edit attempt: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (IllegalArgumentException e) {
            log.error("Error editing post title: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            log.error("Unexpected error while updating post title: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 게시글 내용 수정
     *
     * @param postId           수정할 게시글 ID
     * @param body             새로운 내용을 포함한 request body (JSON 형식)
     * @param loggedInMemberId 요청 사용자 ID
     * @return 수정된 게시글 정보
     */
    @PutMapping("/{postId}/content")
    public ResponseEntity<ForumPostResponseDto> updatePostContent(
            @PathVariable Integer postId,
            @RequestBody Map<String, String> body,
            @RequestParam Integer loggedInMemberId
    ) {
        log.info("Updating post content for ID: {} by member ID: {}", postId, loggedInMemberId);

        boolean isAdmin = memberService.isAdmin(loggedInMemberId); // 관리자 여부 확인
        try {
            var updatedPost = postService.updatePostContent(postId, body.get("content"), loggedInMemberId, isAdmin);
            return ResponseEntity.ok(updatedPost);
        } catch (SecurityException e) {
            log.error("Unauthorized edit attempt: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (IllegalArgumentException e) {
            log.error("Error editing post content: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            log.error("Unexpected error while updating post content: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }





    /**
     * 게시글 삭제
     *
     * @param id 삭제할 게시글 ID
     * @param loggedInMemberId 요청 사용자 ID
     * @param removedBy 삭제를 수행한 사용자 정보 (ADMIN 또는 작성자 이름)
     * @return 성공 상태
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Integer id,
            @RequestParam Integer loggedInMemberId,
            @RequestParam String removedBy
    ) {
        postService.deletePost(id, loggedInMemberId, removedBy);
        return ResponseEntity.ok().build();
    }

    /**
     * 게시글 숨김 처리
     *
     * @param postId 숨길 게시글 ID
     * @return 성공 상태
     */
    @PostMapping("/{postId}/hide")
    public ResponseEntity<Void> hidePost(@PathVariable Integer postId) {
        postService.hidePost(postId); // 게시글 숨김 처리
        return ResponseEntity.ok().build(); // 성공 상태 반환
    }

    /**
     * 숨겨진 게시글 복구
     *
     * @param postId 복구할 게시글 ID
     * @return 성공 상태
     */
    @PostMapping("/{postId}/restore")
    public ResponseEntity<Void> restorePost(@PathVariable Integer postId) {
        postService.restorePost(postId); // 게시글 복구
        return ResponseEntity.ok().build(); // 성공 상태 반환
    }

    // 서비스 레이어에서 undeletePost를 주석처리 했음. restorePost로 역할이 충분하기에 추후 확정되면 삭제.
//    /**
//     * 삭제된 게시글 복구
//     *
//     * @param postId 복구할 게시글 ID
//     * @return 성공 상태
//     */
//    @PostMapping("/{postId}/undelete")
//    public ResponseEntity<Void> undeletePost(@PathVariable Integer postId) {
//        postService.undeletePost(postId); // 삭제된 게시글 복구
//        return ResponseEntity.ok().build(); // 성공 상태 반환
//    }

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
                .orElse(ResponseEntity.notFound().build()); // memberId 포함된 응답 DTO 반환
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

    /**
     * 게시글 신고 처리
     *
     * @param postId 신고할 게시글 ID
     * @param reporterId 신고자 ID
     * @param reason 신고 사유
     * @return 신고 결과 (업데이트된 게시글 정보 포함)
     */
    @PostMapping("/{postId}/report")
    public ResponseEntity<ForumPostResponseDto> reportPost(
            @PathVariable Integer postId,
            @RequestParam Integer reporterId,
            @RequestBody String reason) {
        // 게시글 신고 처리 후 업데이트된 게시글 정보 반환
        ForumPostResponseDto responseDto = postService.reportPost(postId, reporterId, reason);
        return ResponseEntity.ok(responseDto); // 반환 시 업데이트된 데이터를 포함
    }




}
