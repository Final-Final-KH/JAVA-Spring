package com.capstone.project.kedu.entity.comment;

import com.capstone.project.kedu.entity.edu.AcademyEntity2;
import com.capstone.project.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "academy_comments")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class AcademyCommentEntity2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long academy_comment_id;

    private int job; // 학원 수강이 취업에 도움이 되었는지

    private int lecture; // 강의는 좋았는지

    private int facilities; // 시설에 대한 점수

    private int teacher; // 강사님에 대한 점수

    private int books; // 교재는 도움이 되었는지

    private int service; // 취업지원에 대한 점수

    private boolean employee_outcome; // 결과적으로 취업은?

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "academy_id")
    private AcademyEntity2 academyEntity2;

}
