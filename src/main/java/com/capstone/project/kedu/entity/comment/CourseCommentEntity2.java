package com.capstone.project.kedu.entity.comment;

import com.capstone.project.kedu.entity.edu.CourseEntity2;
import com.capstone.project.member.entity.Member;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "lecture_comments")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseCommentEntity2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long course_comment_id;

    private boolean employee_outcome;

    private int job; // 실무적이었는지

    private int lecture; // 강의는 좋았는지

    private int teacher; // 강사님에 대한 점수

    private int books; // 교재는 도움이 되었는지

    @Column(name = "new_tech")
    private int newTech; // 취업 전망과 일치하였는지
    @Column(name ="skill_up")
    private int skillUp;  // 개인의 기술이 발전 했는지

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private CourseEntity2 courseEntity2;
}
