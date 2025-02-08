package com.capstone.project.forum.controller;

import com.capstone.project.forum.dto.request.ForumPostCommentRequestDto;
import com.capstone.project.forum.dto.response.ForumPostCommentResponseDto;
import com.capstone.project.forum.service.ForumPostCommentService;
import com.capstone.project.member.service.MemberService;
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
    private final MemberService memberService;

    /**
     * 특정 게시글의 댓글 가져오기
     *
     * @param postId 게시글 ID
     * @return 댓글 리스트
     */
    @GetMapping("/{postId}")
    public ResponseEntity<List<ForumPostCommentResponseDto>> getCommentsForPost(@PathVariable Integer postId) {
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
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<ForumPostCommentResponseDto> updateComment(
            @PathVariable Integer commentId,
            @RequestBody ForumPostCommentRequestDto requestDto,
            @RequestParam Integer loggedInMemberId
    ) {
        log.info("Updating comment ID: {} by member ID: {}", commentId, loggedInMemberId);
        log.info("Received updateComment request: {}", requestDto);

        try {
            boolean isAdmin = memberService.isAdmin(loggedInMemberId); // 관리자 여부 확인
            ForumPostCommentResponseDto updatedComment = commentService.updateComment(commentId, requestDto, loggedInMemberId, isAdmin);
            return ResponseEntity.ok(updatedComment);
        } catch (SecurityException e) {
            log.error("Unauthorized edit attempt: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("Error editing comment: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            log.error("Unexpected error while updating comment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
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
     * @return 복구된 댓글 데이터
     */
    @PostMapping("/{commentId}/restore")
    public ResponseEntity<ForumPostCommentResponseDto> restoreComment(@PathVariable Integer commentId) {
        ForumPostCommentResponseDto restoredComment = commentService.restoreComment(commentId); // 복구된 댓글 정보 반환
        return ResponseEntity.ok(restoredComment); // 복구된 댓글 데이터 반환
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
     * @param commentId 삭제할 댓글 ID
     * @param loggedInMemberId 요청 사용자 ID
     * @return 성공 상태
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Integer commentId,
            @RequestParam Integer loggedInMemberId // 로그인된 사용자의 ID를 요청 매개변수로 받음
    ) {
        log.info("Deleting comment ID: {} by member ID: {}", commentId, loggedInMemberId);
        commentService.deleteComment(commentId, loggedInMemberId);
        return ResponseEntity.noContent().build(); // 성공적인 삭제 응답 반환
    }

    /**
     * 댓글 신고
     *
     * @param commentId 신고 대상 댓글 ID
     * @param reporterId 신고자 ID
     * @param reason 신고 사유
     * @return 신고 결과 (업데이트된 댓글 정보 포함)
     */
    @PostMapping("/{commentId}/report")
    public ResponseEntity<ForumPostCommentResponseDto> reportComment(
            @PathVariable Integer commentId,
            @RequestParam Integer reporterId,
            @RequestBody String reason) {
        // 댓글 신고 처리 후 업데이트된 댓글 정보 반환
        ForumPostCommentResponseDto responseDto = commentService.reportComment(commentId, reporterId, reason);
        return ResponseEntity.ok(responseDto); // 반환 시 업데이트된 데이터를 포함
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
