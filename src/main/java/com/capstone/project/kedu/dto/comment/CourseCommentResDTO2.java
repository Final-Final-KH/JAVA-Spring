package com.capstone.project.kedu.dto.comment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CourseCommentResDTO2 {
    private Long course_comment_id;
    private Long course_id;
    private int member_id;
    private boolean employee_outcome;
    private double job; // 실무적이었는지
    private double lecture; // 강의는 좋았는지
    private double teacher; // 강사님에 대한 점수
    private double books; // 교재는 도움이 되었는지
    private double newTech; // 취업 전망과 일치하였는지
    private double skillUp;  // 개인의 기술이 발전 했는지

    private double totalAvg; // 전체 평균

}
