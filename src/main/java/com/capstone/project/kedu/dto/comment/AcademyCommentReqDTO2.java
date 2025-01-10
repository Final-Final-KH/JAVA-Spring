package com.capstone.project.kedu.dto.comment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class AcademyCommentReqDTO2 {
    private Long academy_id;
    private boolean employee_outcome;
    private int member_id;
    private int job;
    private int lecture;
    private int facilities;
    private int teacher;
    private int books;
    private int service;

}
