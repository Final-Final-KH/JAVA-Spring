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
@Table(name = "academy")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class AcademyEntity2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "academy_id")
    private Long academyId;

    @Column(name = "academy_name")
    private String academyName;

    @Column(nullable = true)
    private String region; // 예: 서울 강남구

    @OneToMany(mappedBy = "academyEntity2", cascade = CascadeType.ALL, orphanRemoval = true )
    private List<MyCourseEntity2> myCourse = new ArrayList<>();

    @OneToMany(mappedBy = "academyEntity2", cascade = CascadeType.ALL, orphanRemoval = true )
    private List<SurveyEntity2> survey = new ArrayList<>();

    @OneToMany(mappedBy = "academyEntity2", cascade = CascadeType.ALL, orphanRemoval = true )
    private List<MyAcademyEntity2> myAcademy = new ArrayList<>();
}
