package com.capstone.project.feed.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FeedResponseDto {
    private int feedId;
    private int memberId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int likesCount;
    private Integer repostedFrom;

    public FeedResponseDto(int feedId, int memberId, String content, LocalDateTime createdAt,
                           LocalDateTime updatedAt, int likesCount, Integer repostedFrom) {
        this.feedId = feedId;
        this.memberId = memberId;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.likesCount = likesCount;
        this.repostedFrom = repostedFrom;
    }
}