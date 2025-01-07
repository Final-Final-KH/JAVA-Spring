package com.capstone.project.feed.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedResponseDto {
    private Integer feedId; // 피드 ID
    private Integer memberId; // 작성자 ID
    private String content; // 게시물 내용
    private LocalDateTime createdAt; // 생성 시간
    private LocalDateTime updatedAt; // 마지막 수정 시간
    private Integer likesCount; // 좋아요 수
    private Integer repostedFrom; // 원본 피드 ID
    private String repostedFromContent; // 원본 피드 내용
    private List<CommentResponseDto> comments; // 댓글 리스트
}