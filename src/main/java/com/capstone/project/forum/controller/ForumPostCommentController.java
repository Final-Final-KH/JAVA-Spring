package com.capstone.project.forum.controller;

import com.capstone.project.forum.dto.request.ForumPostCommentRequestDto;
import com.capstone.project.forum.dto.response.ForumPostCommentResponseDto;
import com.capstone.project.forum.service.ForumPostCommentService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 댓글 컨트롤러 클래스
 * REST API 엔드포인트를 정의하고 Service 계층 호출
 */
@RestController
@RequestMapping("/api/forums/comments")
@RequiredArgsConstructor
@Slf4j
public class ForumPostCommentController {

    private final ForumPostCommentService commentService; // 댓글 서비스 의존성 주입

    /**
     * 특정 게시글의 댓글 가져오기
     *
     * @param postId 게시글 ID
     * @return 댓글 리스트
     */
    @GetMapping
    public ResponseEntity<List<ForumPostCommentResponseDto>> getCommentsForPost(@RequestParam Integer postId) {
        // Service 호출로 댓글 리스트 반환
        return ResponseEntity.ok(commentService.getCommentsForPost(postId));
    }

    /**
     * 댓글 생성
     *
     * @param requestDto 댓글 생성 요청 DTO
     * @return 생성된 댓글 정보
     */
    @PostMapping
    public ResponseEntity<ForumPostCommentResponseDto> createComment(
            @RequestBody ForumPostCommentRequestDto requestDto) {
        log.info("Creating comment for post ID: {}", requestDto.getPostId());
        ForumPostCommentResponseDto responseDto = commentService.createComment(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * 댓글 수정
     *
     * @param commentId 수정할 댓글 ID
     * @param newContent 수정할 댓글 내용 (JSON 또는 단순 텍스트)
     * @return 수정된 댓글 정보
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<ForumPostCommentResponseDto> updateComment(
            @PathVariable Integer commentId,
            @RequestBody String newContent
    ) {
        // JSON 및 단순 텍스트 처리
        String sanitizedContent;
        if (newContent.trim().startsWith("{") && newContent.trim().endsWith("}")) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(newContent);
                sanitizedContent = jsonNode.get("newContent").asText();
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid JSON format for newContent");
            }
        } else {
            sanitizedContent = newContent.trim();
        }

        if (sanitizedContent == null || sanitizedContent.isEmpty()) {
            throw new IllegalArgumentException("Content cannot be empty.");
        }

        ForumPostCommentResponseDto updatedComment = commentService.updateComment(commentId, sanitizedContent);
        return ResponseEntity.ok(updatedComment);
    }




    /**
     * 댓글 숨김 처리
     *
     * @param commentId 숨길 댓글 ID
     * @return 성공 상태
     */
    @PostMapping("/{commentId}/hide")
    public ResponseEntity<Void> hideComment(@PathVariable Integer commentId) {
        commentService.hideComment(commentId); // 댓글 숨김 처리
        return ResponseEntity.ok().build(); // 성공 상태 반환
    }

    /**
     * 숨겨진 댓글 복구
     *
     * @param commentId 복구할 댓글 ID
     * @return 성공 상태
     */
    @PostMapping("/{commentId}/restore")
    public ResponseEntity<Void> restoreComment(@PathVariable Integer commentId) {
        commentService.restoreComment(commentId); // 댓글 복구
        return ResponseEntity.ok().build(); // 성공 상태 반환
    }


    //    게시글/포스팅쪽 이랑 동일한 문제. 중복된 기능으로 판단되서 주석처리
    // 추후에 확정되면 삭제 처리
//    /**
//     * 삭제된 댓글 복구
//     *
//     * @param commentId 복구할 댓글 ID
//     * @return 성공 상태
//     */
//    @PostMapping("/{commentId}/undelete")
//    public ResponseEntity<Void> undeleteComment(@PathVariable Integer commentId) {
//        commentService.undeleteComment(commentId); // 삭제 취소 호출
//        return ResponseEntity.ok().build();
//    }



    /**
     * 댓글 삭제
     *
     * @param id 삭제할 댓글 ID
     * @param loggedInMemberId 요청 사용자 ID
     * @return 성공 상태
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Integer id,
            @RequestParam Integer loggedInMemberId
    ) {
        commentService.deleteComment(id, loggedInMemberId);
        return ResponseEntity.ok().build();
    }

    /**
     * 댓글 신고
     *
     * @param commentId 신고 대상 댓글 ID
     * @param reporterId 신고자 ID
     * @param reason 신고 사유
     * @return 성공 상태
     */
    @PostMapping("/{commentId}/report")
    public ResponseEntity<Void> reportComment(
            @PathVariable Integer commentId,
            @RequestParam Integer reporterId,
            @RequestBody String reason) {
        commentService.reportComment(commentId, reporterId, reason);
        return ResponseEntity.ok().build();
    }


    /**
     * 댓글에 대한 답글 추가 (인용)
     *
     * @param commentId 부모 댓글 ID
     * @param requestDto 답글 요청 데이터
     * @return 생성된 답글 정보
     */
    @PostMapping("/{commentId}/reply")
    public ResponseEntity<ForumPostCommentResponseDto> replyToComment(
            @PathVariable Integer commentId,
            @RequestBody ForumPostCommentRequestDto requestDto
    ) {
        // Service 호출로 댓글에 대한 답글 생성
        return ResponseEntity.ok(commentService.replyToComment(commentId, requestDto));
    }

    /**
     * 게시글(OP)에 대한 답글 추가 (인용)
     *
     * @param postId 게시글 ID
     * @param requestDto 답글 요청 데이터
     * @return 생성된 답글 정보
     */
    @PostMapping("/post/{postId}/reply")
    public ResponseEntity<ForumPostCommentResponseDto> replyToPost(
            @PathVariable Integer postId,
            @RequestBody ForumPostCommentRequestDto requestDto
    ) {
        // Service 호출로 게시글(OP)에 대한 답글 생성
        return ResponseEntity.ok(commentService.replyToPost(postId, requestDto));
    }




}
