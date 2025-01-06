package com.capstone.project.kedu.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class KeduResDTO2 {
    private Long courseId;           // 코스 ID
    private String academyName;      // 학원 이름
    private String courseName;       // 코스 이름
    private Date startDate;          // 시작 날짜
    private Date endDate;            // 종료 날짜
    private String region;           // 지역
    private String auth;             // 인증 상태
    private int trDate;             // 등록 날짜
    private int totalHour;           // 총 교육 시간
    private int employmentRate;      // 취업률
    private int priceTotal;          // 총 비용
    private int selfPayment;         // 자부담 비용
}