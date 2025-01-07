package com.capstone.project.feed.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedRequestDto {
    private Integer memberId; // 작성자 ID
    private String content; // 게시물 내용
}