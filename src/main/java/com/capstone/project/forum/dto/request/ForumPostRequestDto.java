package com.capstone.project.forum.dto.request;

import lombok.Data;

import java.util.List;

/**
 * 새로운 게시글 생성을 위한 요청 DTO
 */
@Data
public class ForumPostRequestDto {
    private Integer categoryId; // 카테고리 ID
    private Integer memberId; // 작성자 ID
    private String title; // 게시글 제목
    private String content; // 게시글 내용
    private Boolean sticky = false; // 상단 고정 여부 기본값 추가

    private List<String> fileUrls; // 첨부 파일 URL 목록 (단일 또는 다중 모두 가능)
}


