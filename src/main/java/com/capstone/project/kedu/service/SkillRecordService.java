package com.capstone.project.kedu.service;

import com.capstone.project.kedu.entity.mypage.SkillRecordEntity;
import com.capstone.project.kedu.repository.mypage.SkillRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SkillRecordService {

    @Autowired
    private SkillRecordRepository skillRecordRepository;

    // 특정 날짜에 해당하는 기술 연습 기록 조회
    public List<SkillRecordEntity> getSkillRecordsByDate(int memberId, LocalDate date) {
        return skillRecordRepository.findByMemberIdAndDate(memberId, date);
    }

    // 날짜 범위에 해당하는 기술 연습 기록 조회
    public List<SkillRecordEntity> getSkillRecordsByDateRange(int memberId, LocalDate startDate, LocalDate endDate) {
        return skillRecordRepository.findByMemberIdAndDateBetween(memberId, startDate, endDate);
    }

    // 특정 회원의 모든 기술 연습 기록 조회
    public List<SkillRecordEntity> getAllSkillRecordsByMember(int memberId) {
        return skillRecordRepository.findByMemberId(memberId);
    }
}
