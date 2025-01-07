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


}
