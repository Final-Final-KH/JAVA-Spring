package com.capstone.project.forum.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 게시글 정보를 전송하기 위한 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForumPostResponseDto {
    private Integer id; // 게시글 ID
    private String title; // 게시글 제목
    private String content; // 게시글 내용
    private String authorName; // 작성자 이름
    private Boolean sticky; // 상단 고정 여부
    private Integer viewsCount; // 조회수
    private Integer likesCount; // 좋아요 수
    private LocalDateTime createdAt; // 생성 시간
    private LocalDateTime updatedAt; // 수정 시간
}
