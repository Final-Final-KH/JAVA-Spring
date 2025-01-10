package com.capstone.project.forum.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 포럼 카테고리 정보를 전송하기 위한 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForumCategoryDto {
    private Integer id; // 카테고리 ID
    private String title; // 카테고리 제목
    private String description; // 카테고리 설명
    private Integer postCount; // 카테고리에 속한 게시글 수
    private Integer commentCount; // 카테고리에 속한 모든 댓글 수
    private LocalDateTime updatedAt; // 마지막 수정 시간
}
