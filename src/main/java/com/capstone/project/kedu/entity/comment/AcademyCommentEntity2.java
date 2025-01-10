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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long academy_comment_id;

    private boolean employee_outcome;

    private String comment;

    private String pros;

    private String cons;

    private int satisfaction;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "academy_id")
    private AcademyEntity2 academyEntity2;

}
