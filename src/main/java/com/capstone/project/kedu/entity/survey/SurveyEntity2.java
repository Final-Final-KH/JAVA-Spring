package com.capstone.project.kedu.entity.survey;

import com.capstone.project.kedu.entity.edu.AcademyEntity2;
import com.capstone.project.kedu.entity.edu.CourseEntity2;
import com.capstone.project.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "survey")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class SurveyEntity2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long survey_id;

    private String teacher;

    private String lecture;

    private String facilities;

    private String comment; // 수업 난이도

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "academy_id")
    private AcademyEntity2 academyEntity2;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private CourseEntity2 courseEntity2;
}
