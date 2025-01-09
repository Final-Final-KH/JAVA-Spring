package com.capstone.project.kedu.entity.comment;

import com.capstone.project.kedu.entity.edu.CourseEntity2;
import com.capstone.project.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "lecture_comments")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class CourseCommentEntity2 {
    @Id
    private Long course_comment_id;

    private String teacher;

    private String lecture;

    private boolean employee_outcome;

    private String facilities;

    private String contents; // 강의 주제에 대한 피드백

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private CourseEntity2 courseEntity2;
}
