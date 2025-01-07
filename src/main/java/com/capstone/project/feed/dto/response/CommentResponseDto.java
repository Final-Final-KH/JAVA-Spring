package com.capstone.project.feed.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponseDto {
    private Integer commentId; // 댓글 ID
    private Integer memberId; // 댓글 작성자 ID
    private String comment; // 댓글 내용
    private LocalDateTime createdAt; // 댓글 생성 시간
    private LocalDateTime updatedAt; // 댓글 수정 시간
}