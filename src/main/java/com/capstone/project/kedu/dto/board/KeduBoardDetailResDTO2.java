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
public class KeduBoardDetailResDTO2 {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime regDate;
    private String user_id;

}
