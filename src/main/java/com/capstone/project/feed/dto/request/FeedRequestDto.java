package com.capstone.project.feed.dto.request;

import lombok.Data;

@Data
public class FeedRequestDto {

    private int memberId; // 작성자 ID
    private String content; // 피드 내용
    private Integer repostedFrom; // 리포스트 원본 피드 ID (nullable)
}
