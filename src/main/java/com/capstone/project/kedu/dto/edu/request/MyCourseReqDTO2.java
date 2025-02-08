package com.capstone.project.kedu.dto.edu.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class MyCourseReqDTO2 {
    private int member_id;
    private Long academy_id;
    private Long course_id;
    private String academy;
    private String course;
}
