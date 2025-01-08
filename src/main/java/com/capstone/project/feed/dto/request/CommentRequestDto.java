package com.capstone.project.feed.dto.request;

import lombok.Getter;
import lombok.Setter;

// 댓글 요청 데이터 전송 객체
@Getter
@Setter
public class CommentRequestDto {
    private Integer memberId; // 작성자 ID
    private String comment;   // 댓글 내용
}
