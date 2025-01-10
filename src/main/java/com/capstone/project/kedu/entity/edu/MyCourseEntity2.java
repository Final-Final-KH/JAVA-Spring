package com.capstone.project.kedu.entity.edu;

import com.capstone.project.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "my_course_list")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class MyCourseEntity2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long list_id;

    @ManyToOne
    @JoinColumn(name = "academy_id")
    private AcademyEntity2 academyEntity2;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private CourseEntity2 courseEntity2;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
}
