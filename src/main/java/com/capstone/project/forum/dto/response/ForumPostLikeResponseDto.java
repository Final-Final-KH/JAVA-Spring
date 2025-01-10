package com.capstone.project.forum.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 좋아요 관련 정보를 전송하기 위한 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForumPostLikeResponseDto {
    private boolean liked; // 좋아요 여부
    private Integer totalLikes; // 총 좋아요 수
}
