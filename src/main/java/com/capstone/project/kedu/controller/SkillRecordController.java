package com.capstone.project.kedu.controller;

import com.capstone.project.kedu.entity.mypage.SkillRecordEntity;
import com.capstone.project.kedu.service.SkillRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/mypage/skill")
public class SkillRecordController {

    @Autowired
    private SkillRecordService skillRecordService;

    // 특정 날짜에 해당하는 기술 연습 기록 조회
    @PostMapping("/records/{memberId}/{date}")
    public ResponseEntity<List<SkillRecordEntity>> getSkillRecordsByDate(
            @PathVariable int memberId, @PathVariable String date) {
        LocalDate localDate = LocalDate.parse(date);  // 날짜 문자열을 LocalDate로 변환
        List<SkillRecordEntity> skillRecords = skillRecordService.getSkillRecordsByDate(memberId, localDate);
        return ResponseEntity.ok(skillRecords);
    }

    // 날짜 범위에 해당하는 기술 연습 기록 조회
    @PostMapping("/records/{memberId}/{startDate}/{endDate}")
    public ResponseEntity<List<SkillRecordEntity>> getSkillRecordsByDateRange(
            @PathVariable int memberId, @PathVariable String startDate, @PathVariable String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        List<SkillRecordEntity> skillRecords = skillRecordService.getSkillRecordsByDateRange(memberId, start, end);
        return ResponseEntity.ok(skillRecords);
    }

    // 특정 회원의 모든 기술 연습 기록 조회
    @PostMapping("/records/{memberId}")
    public ResponseEntity<List<SkillRecordEntity>> getAllSkillRecordsByMember(@PathVariable int memberId) {
        List<SkillRecordEntity> skillRecords = skillRecordService.getAllSkillRecordsByMember(memberId);
        return ResponseEntity.ok(skillRecords);
    }
}
