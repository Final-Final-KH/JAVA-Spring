package com.capstone.project.forum.dto.request;

import lombok.Data;

/**
 * 좋아요 요청을 위한 DTO
 */
@Data
public class ForumPostLikeRequestDto {

    /**
     * 좋아요 요청을 보낸 사용자 ID
     */
    private Integer memberId;

    /**
     * 좋아요 대상 게시글 ID
     */
    private Integer postId;

    /**
     * 좋아요 대상 댓글 ID
     */
    private Integer commentId;
}
