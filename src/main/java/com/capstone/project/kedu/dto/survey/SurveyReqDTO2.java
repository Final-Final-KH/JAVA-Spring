package com.capstone.project.kedu.dto.survey;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class SurveyReqDTO2 {
    private String teacher;

    private String lecture;

    private String facilities;

    private String comment;

    private int member_id;

    private Long academy_id;

    private Long course_id;
}
