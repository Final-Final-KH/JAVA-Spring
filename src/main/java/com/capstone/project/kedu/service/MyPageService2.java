package com.capstone.project.kedu.service;

import com.capstone.project.kedu.dto.mypage.SkillHubReqDTO2;
import com.capstone.project.kedu.dto.mypage.SkillHubResDTO2;
import com.capstone.project.kedu.entity.mypage.SkillHubEntity2;
import com.capstone.project.kedu.repository.board.KeduBoardCommentCommentRepository2;
import com.capstone.project.kedu.repository.board.KeduBoardCommentRepository2;
import com.capstone.project.kedu.repository.board.KeduBoardRepository2;
import com.capstone.project.kedu.repository.comment.AcademyCommentRepository2;
import com.capstone.project.kedu.repository.comment.CourseCommentRepository2;
import com.capstone.project.kedu.repository.edu.AcademyRepository2;
import com.capstone.project.kedu.repository.edu.CourseRepository2;
import com.capstone.project.kedu.repository.edu.MyCourseRepository2;
import com.capstone.project.kedu.repository.mypage.SkillHubRepository2;
import com.capstone.project.member.entity.Member;
import com.capstone.project.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyPageService2 {
    private final MemberRepository memberRepository;
    private final CourseRepository2 courseRepository2;
    private final AcademyRepository2 academyRepository2;
    private final KeduBoardRepository2 keduBoardRepository2;
    private final KeduBoardCommentRepository2 keduBoardCommentRepository2;
    private final KeduBoardCommentCommentRepository2 keduBoardCommentCommentRepository2;
    private final MyCourseRepository2 myCourseRepository2;
    private final AcademyCommentRepository2 academyCommentRepository2;
    private final CourseCommentRepository2 courseCommentRepository2;
    private final SkillHubRepository2 skillHubRepository2;


    public boolean addSkill(SkillHubReqDTO2 skillHubReqDTO2) {
        try {
            Member member = memberRepository.findById(skillHubReqDTO2.getMemberId())
                    .orElseThrow(() -> new RuntimeException("회원 정보가 존재하지 않습니다."));

            SkillHubEntity2 skillHubEntity = new SkillHubEntity2();
            skillHubEntity.setSkillType(skillHubReqDTO2.getType());
            skillHubEntity.setSkillPoints(skillHubReqDTO2.getPoints());
            skillHubEntity.setMember(member);
            skillHubEntity.setDate(LocalDate.now());  // 현재 날짜를 자동으로 기록

            skillHubRepository2.save(skillHubEntity);  // 기술 활동 저장

            return true;
        } catch (Exception e) {
            log.error("기술 연습 추가 실패 : {}", e.getMessage());
            return false;
        }
    }


    public boolean update(SkillHubReqDTO2 skillHubReqDTO2, Long id) {
        try {
            // 기술연습일지 엔티티 조회
            SkillHubEntity2 skillHubEntity2 = skillHubRepository2.findById(id)
                    .orElseThrow(() -> new RuntimeException("해당 기술연습일지가 존재하지 않습니다."));

            // 회원 엔티티 조회
            Member member = memberRepository.findById(skillHubReqDTO2.getMemberId())
                    .orElseThrow(() -> new RuntimeException("해당 회원이 존재하지 않습니다."));

            // 수정할 회원인지 확인
            if (skillHubEntity2.getMember().getMemberId().equals(skillHubReqDTO2.getMemberId())) {
                // 스킬 포인트 업데이트
                skillHubEntity2.setSkillPoints(skillHubReqDTO2.getPoints());

                // 현재 날짜로 date 필드 업데이트
                skillHubEntity2.setDate(LocalDate.now());  // 현재 날짜로 설정

                // 수정된 엔티티 저장
                skillHubRepository2.save(skillHubEntity2);
                return true;
            } else {
                log.error("게시글은 작성자만 수정할 수 있습니다.");
                return false;
            }
        } catch (Exception e) {
            log.error("게시글 수정 실패 : {}", e.getMessage());
            return false;
        }
    }


    public boolean delete(int memberId, Long id) {
        try {
            // 기술연습일지 엔티티 조회
            SkillHubEntity2 skillHubEntity2 = skillHubRepository2.findById(id)
                    .orElseThrow(() -> new RuntimeException("해당 기술연습일지가 존재하지 않습니다."));

            // 회원 엔티티 조회
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new RuntimeException("해당 회원이 존재하지 않습니다."));

            // 삭제 권한 확인
            if (!skillHubEntity2.getMember().getMemberId().equals(memberId)) {
                throw new RuntimeException("게시물 삭제 권한이 없습니다.");
            }

            // 기술연습일지 삭제
            skillHubRepository2.delete(skillHubEntity2);
            return true;
        } catch (Exception e) {
            log.error("게시물 삭제에 실패했습니다. : {}", e.getMessage());
            return false;
        }
    }

    public List<SkillHubResDTO2> list(int memberId) {
        List<SkillHubEntity2> skillHubEntity2s =
                skillHubRepository2.findByMemberId(memberId);
        List<SkillHubResDTO2>skillHubResDTO2List = new ArrayList<>();
        for(SkillHubEntity2 skillHubEntity2 : skillHubEntity2s){
            skillHubResDTO2List.add(convertEntityToDTO(skillHubEntity2));
        }
        return skillHubResDTO2List;
    }

    private SkillHubResDTO2 convertEntityToDTO(SkillHubEntity2 skillHubEntity2) {
        SkillHubResDTO2 skillHubResDTO2 = new SkillHubResDTO2();
        skillHubResDTO2.setId(skillHubEntity2.getId());
        skillHubResDTO2.setPoints(skillHubEntity2.getSkillPoints());
        skillHubResDTO2.setType(skillHubEntity2.getSkillType());
        skillHubResDTO2.setMemberId(skillHubEntity2.getMember().getMemberId());
        skillHubResDTO2.setDate(skillHubEntity2.getDate());
        return skillHubResDTO2;
    }
}
