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
    private int member_id;
    private double avgJob; // job 평균
    private double avgLecture; // lecture 평균
    private double avgFacilities; // facilities 평균
    private double avgTeacher; // teacher 평균
    private double avgBooks; // books 평균
    private double avgService; // service 평균

    private double totalAvg; // 전체 평균
}
