package com.capstone.project.kedu.dto.mypage;

import com.capstone.project.kedu.entity.mypage.SkillType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class SkillHubResDTO2 {
    private Long id;           // 스킬 활동의 고유 ID
    private int memberId;     // 활동을 수행한 회원의 ID
    private SkillType type;    // 활동의 타입
    private int points;        // 활동의 점수
    private LocalDate date;
}
