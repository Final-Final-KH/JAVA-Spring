package com.capstone.project.kedu.dto.board;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class KeduBoardCommentResDTO2 {
    private Long board_comment_id;
    private Long board_id;
    private int member_id;
    private String content;
    private LocalDateTime regDate;
}
