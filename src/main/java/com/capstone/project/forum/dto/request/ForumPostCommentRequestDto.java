package com.capstone.project.forum.dto.request;

import lombok.Data;

/**
 * 새로운 댓글 생성을 위한 요청 DTO
 */
@Data
public class ForumPostCommentRequestDto {
    private Integer postId; // 게시글 ID
    private Integer memberId; // 작성자 ID
    private String content; // 댓글 내용

    // 추가 필드: OP 작성자의 이름 (인용 시 필요)
    private String opAuthorName;

    // 추가 필드: OP 내용 (인용 시 필요)
    private String opContent;
}
