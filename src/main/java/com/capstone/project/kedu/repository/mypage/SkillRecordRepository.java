package com.capstone.project.kedu.repository.mypage;

import com.capstone.project.kedu.entity.mypage.SkillRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SkillRecordRepository extends JpaRepository<SkillRecordEntity, Long> {

    List<SkillRecordEntity> findByMemberIdAndDate(int memberId, LocalDate date);  // 특정 날짜에 대한 기록 조회

    List<SkillRecordEntity> findByMemberIdAndDateBetween(int memberId, LocalDate startDate, LocalDate endDate);  // 날짜 범위에 대한 기록 조회

    List<SkillRecordEntity> findByMemberId(int memberId);  // 특정 회원의 모든 기록 조회
}
