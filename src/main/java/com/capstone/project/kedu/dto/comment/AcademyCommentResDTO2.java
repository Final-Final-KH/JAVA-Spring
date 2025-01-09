package com.capstone.project.kedu.dto.comment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class AcademyCommentResDTO2 {
    private Long academy_comment_id;
    private Long academy_id;
    private boolean employee_outcome;
    private int member_id;
    private String comment;
    private String pros;
    private String cons;
    private int satisfaction;
}
