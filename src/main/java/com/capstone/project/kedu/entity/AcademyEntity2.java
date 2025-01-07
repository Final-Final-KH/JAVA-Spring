package com.capstone.project.kedu.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
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

    @Column(nullable = false)
    private String academyName;

    @Column(nullable = true)
    private String region; // 예: 서울 강남구

    @OneToMany(mappedBy = "academy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CourseEntity2> courses; // 해당 기관에서 제공하는 강의 리스트


}
