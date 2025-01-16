package com.capstone.project.forum.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 댓글 응답 DTO
 * API 응답에 필요한 댓글 정보를 포함
 */
@Data
@Builder
@AllArgsConstructor
public class ForumPostCommentResponseDto {

    private Integer id; // 댓글 ID
    private String content; // 댓글 내용
    private String authorName; // 작성자 이름
    private Integer likesCount; // 좋아요 수
    private Boolean hidden; // 숨김 여부
    private String removedBy; // 삭제자 정보
    private LocalDateTime createdAt; // 생성 시간
    private LocalDateTime updatedAt; // 수정 시간
    private String fileUrl; // 첨부 파일 URL
}
