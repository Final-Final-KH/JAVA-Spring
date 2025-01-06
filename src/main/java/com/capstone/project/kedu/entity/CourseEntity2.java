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

    @ManyToOne
    @JoinColumn(name = "academy_id", nullable = false)
    private AcademyEntity2 academy; // 강의와 관련된 기관

    @Column(nullable = false)
    private String courseName; // 강의명

    private String auth; // 인증 정보
    private Date startDate;
    private Date endDate;
    private int totalHour;
    private double priceTotal;
    private double selfPayment;

    // getters and setters
}
