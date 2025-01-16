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
public class KeduBoardResDTO2 {
    private Long id;
    private String title;
    private String user_id;
    private LocalDateTime regDate;
    private String content;

}
