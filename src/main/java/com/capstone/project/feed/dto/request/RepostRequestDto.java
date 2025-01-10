package com.capstone.project.feed.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepostRequestDto {
    private String content;   // 리포스트 시 추가할 내용 (선택 사항)
    private String mediaUrl;  // 리포스트 시 미디어 파일 URL (선택 사항)
}
