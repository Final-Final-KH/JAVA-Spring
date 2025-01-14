package com.capstone.project.forum.dto.request;

import lombok.Data;

/**
 * 새로운 게시글 생성을 위한 요청 DTO
 */
@Data
public class ForumPostRequestDto {
    private Integer categoryId; // 카테고리 ID
    private Integer memberId; // 작성자 ID
    private String title; // 게시글 제목
    private String content; // 게시글 내용
    private Boolean sticky; // 상단 고정 여부
}
