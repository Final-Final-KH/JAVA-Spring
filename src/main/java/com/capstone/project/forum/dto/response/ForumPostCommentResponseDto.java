package com.capstone.project.forum.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 댓글 정보를 전송하기 위한 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForumPostCommentResponseDto {
    private Integer id; // 댓글 ID
    private String content; // 댓글 내용
    private String authorName; // 작성자 이름
    private Integer likesCount; // 좋아요 수
    private LocalDateTime createdAt; // 생성 시간
}
