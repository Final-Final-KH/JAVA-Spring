package com.capstone.project.kedu.dto.comment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CourseCommentReqDTO2 {
    private boolean employee_outcome;

    private int job; // 실무적이었는지

    private int lecture; // 강의는 좋았는지

    private int teacher; // 강사님에 대한 점수

    private int books; // 교재는 도움이 되었는지

    private int newTech; // 취업 전망과 일치하였는지

    private int skillUp;  // 개인의 기술이 발전 했는지

    private int member_id;

    private Long course_id;
}
