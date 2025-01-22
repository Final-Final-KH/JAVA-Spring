package com.capstone.project.kedu.dto.edu.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
@Getter
@Setter
@ToString
@NoArgsConstructor
public class CourseDetailResDTO2 {

    // 공통 코드
    private int member_id; // member_id
    private Long academy_id; // academy_id
    private Long course_id; // course_id

    // 학원 리뷰 AcademyCommentRepository2 이 레포지토리에는 course_id가 없음
    private int academy_avgJob; // job 평균 // job
    private int academy_avgLecture; // lecture 평균 // lecture
    private int academy_avgFacilities; // facilities 평균 // facilities
    private int academy_avgTeacher; // teacher 평균 // teacher
    private int academy_avgBooks; // books 평균 // books
    private int academy_avgService; // service 평균 // service

    // 강의 리뷰 CourseCommentRepository2
    private int lecture_job; // 실무적이었는지 // job
    private int lecture_lecture; // 강의는 좋았는지 // lecture
    private int lecture_teacher; // 강사님에 대한 점수 // teacher
    private int lecture_books; // 교재는 도움이 되었는지 // books
    private int lecture_newTech; // 취업 전망과 일치하였는지 // new_tech
    private int lecture_skillUp;  // 개인의 기술이 발전 했는지 // skill_up

    // 한줄 코멘트 KeduBoardRepository2
    private Long comment_id; // board_id
    private String comment_title; // title
    private String comment_user_id; // reg_date
    private LocalDateTime comment_regDate; // reg_date
    private String comment_content; // content

    // 설문조사 SurveyRepository2
    private Long survey_id;  // survey_id
    private String survey_teacher; // teacher
    private String survey_lecture; // lecture
    private String survey_facilities; // facilities
    private String survey_comment; // comment

    // 생성자 추가
    public CourseDetailResDTO2(
            int academy_avgJob, int academy_avgLecture, int academy_avgFacilities, int academy_avgTeacher,
            int academy_avgBooks, int academy_avgService,
            int lecture_job, int lecture_lecture, int lecture_teacher, int lecture_books,
            int lecture_newTech, int lecture_skillUp,
            Long comment_id, String comment_title, String comment_user_id,
            LocalDateTime comment_regDate, String comment_content,
            Long survey_id, String survey_teacher, String survey_lecture,
            String survey_facilities, String survey_comment
    ) {
        this.academy_avgJob = academy_avgJob;
        this.academy_avgLecture = academy_avgLecture;
        this.academy_avgFacilities = academy_avgFacilities;
        this.academy_avgTeacher = academy_avgTeacher;
        this.academy_avgBooks = academy_avgBooks;
        this.academy_avgService = academy_avgService;
        this.lecture_job = lecture_job;
        this.lecture_lecture = lecture_lecture;
        this.lecture_teacher = lecture_teacher;
        this.lecture_books = lecture_books;
        this.lecture_newTech = lecture_newTech;
        this.lecture_skillUp = lecture_skillUp;
        this.comment_id = comment_id;
        this.comment_title = comment_title;
        this.comment_user_id = comment_user_id;
        this.comment_regDate = comment_regDate;
        this.comment_content = comment_content;
        this.survey_id = survey_id;
        this.survey_teacher = survey_teacher;
        this.survey_lecture = survey_lecture;
        this.survey_facilities = survey_facilities;
        this.survey_comment = survey_comment;
    }
}