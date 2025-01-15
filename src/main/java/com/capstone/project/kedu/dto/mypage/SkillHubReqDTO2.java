package com.capstone.project.kedu.dto.mypage;

import com.capstone.project.kedu.entity.mypage.SkillType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class SkillHubReqDTO2 {
    private int memberId;     // 활동을 추가할 회원의 ID
    private SkillType type;    // 활동의 타입
    private int points;        // 활동의 점수 또는 포인트
}
