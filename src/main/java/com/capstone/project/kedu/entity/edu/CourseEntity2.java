package com.capstone.project.kedu.entity.edu;

import com.capstone.project.kedu.entity.survey.SurveyEntity2;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "course")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class CourseEntity2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long courseId;

    private String academy;

    @Column(nullable = false, name = "course_name")
    private String courseName; // 강의명

    private String region;

    @OneToMany(mappedBy = "courseEntity2", cascade = CascadeType.ALL, orphanRemoval = true )
    private List<MyCourseEntity2> myCourse = new ArrayList<>();

    @OneToMany(mappedBy = "courseEntity2", cascade = CascadeType.ALL, orphanRemoval = true )
    private List<SurveyEntity2> survey = new ArrayList<>();
}
