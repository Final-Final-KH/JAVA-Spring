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
public class KeduBoardRegReqDTO2 {
    private String title;
    private String content;
    private Long academy_id;
    private Long course_id;
    private LocalDateTime regDate;
    private String user_id;
    private int member_id;
}
