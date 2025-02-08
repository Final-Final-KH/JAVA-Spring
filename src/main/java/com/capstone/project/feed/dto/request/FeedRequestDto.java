package com.capstone.project.feed.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedRequestDto {
    private Integer memberId; // 작성자 ID
    private String content;   // 게시물 내용
    private String mediaUrl;  // 미디어 파일 URL (선택 사항)
}
