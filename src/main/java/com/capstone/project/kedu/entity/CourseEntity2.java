package com.capstone.project.kedu.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "course")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class CourseEntity2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseId;

    private String academy;

    @Column(nullable = false, name = "course_name")
    private String courseName; // 강의명

    private String region;

}
