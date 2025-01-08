package com.capstone.project.kedu.dto.edu.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class MyCourseResDTO2 {
    private Long list_id;
    private int member_id;
    private Long academy_id;
    private Long course_id;
    private String academy;
    private String course;
}
