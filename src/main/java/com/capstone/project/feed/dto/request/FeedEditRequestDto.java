package com.capstone.project.feed.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO for editing a feed post.
 * 피드 게시물을 수정하기 위한 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeedEditRequestDto {
    private Integer memberId; // ID of the member requesting the edit / 수정을 요청한 회원 ID
    private String newContent; // New content for the post / 게시물의 새 내용
}
