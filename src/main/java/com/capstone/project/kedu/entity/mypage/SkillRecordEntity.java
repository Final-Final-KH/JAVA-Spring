package com.capstone.project.kedu.entity.mypage;

import com.capstone.project.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "skill_record", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"member_id", "date"})  // member와 date가 중복되지 않도록
})
@Getter
@Setter
@NoArgsConstructor
public class SkillRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;  // 어떤 회원의 기록인지

    @Enumerated(EnumType.STRING)
    private SkillType skillType;  // 기술 유형 (예: 프로그래밍, 디자인 등)

    private int points;  // 해당 날짜에 기록된 포인트

    private LocalDate date;  // 날짜 (하루에 한 번 기록됨)

}
