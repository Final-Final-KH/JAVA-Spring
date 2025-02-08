package com.capstone.project.kedu.entity.mypage;

import com.capstone.project.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "skill", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"member_id", "type", "date"})  // 날짜별로 유니크 제약 조건 추가
})
@Getter
@Setter
@ToString(exclude = "member")
@NoArgsConstructor
public class SkillHubEntity2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")  // 명시적으로 'type' 컬럼으로 매핑
    private SkillType skillType;  // Enum으로 타입 정의

    private int skillPoints;  // 점수 필드

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDate date;  // 활동 날짜 추가

}
