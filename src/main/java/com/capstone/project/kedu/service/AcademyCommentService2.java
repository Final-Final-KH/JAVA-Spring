package com.capstone.project.kedu.service;

import com.capstone.project.kedu.dto.comment.AcademyCommentReqDTO2;
import com.capstone.project.kedu.entity.comment.AcademyCommentEntity2;
import com.capstone.project.kedu.entity.edu.AcademyEntity2;
import com.capstone.project.kedu.repository.comment.AcademyCommentRepository2;
import com.capstone.project.kedu.repository.edu.AcademyRepository2;
import com.capstone.project.member.entity.Member;
import com.capstone.project.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AcademyCommentService2 {
    private final AcademyCommentRepository2 academyCommentRepository2;
    private final MemberRepository memberRepository;
    private final AcademyRepository2 academyRepository2;

    public AcademyCommentService2(AcademyCommentRepository2 academyCommentRepository2, MemberRepository memberRepository, AcademyRepository2 academyRepository2) {
        this.academyCommentRepository2 = academyCommentRepository2;
        this.memberRepository = memberRepository;
        this.academyRepository2 = academyRepository2;
    }

    // 학원에 대한 간략한 코멘트 생성
    public boolean create(AcademyCommentReqDTO2 academyCommentReqDTO2) {
        try{
            Member member = memberRepository.findById(academyCommentReqDTO2.getMember_id())
                    .orElseThrow(()-> new RuntimeException("해당 회원이 존재 하지 않습니다."));
            AcademyEntity2 academyEntity2 = academyRepository2.findById(academyCommentReqDTO2.getAcademy_id())
                    .orElseThrow(()-> new RuntimeException("해당 학원이 존재하지 않습니다."));
            AcademyCommentEntity2 academyCommentEntity2 = new AcademyCommentEntity2();
            academyCommentEntity2.setComment(academyCommentReqDTO2.getComment());
            academyCommentEntity2.setAcademyEntity2(academyEntity2);
            academyCommentEntity2.setMember(member);
            academyCommentEntity2.setCons(academyCommentReqDTO2.getCons());
            academyCommentEntity2.setPros(academyCommentReqDTO2.getPros());
            academyCommentEntity2.setSatisfaction(academyCommentReqDTO2.getSatisfaction());
            academyCommentEntity2.setEmployee_outcome(academyCommentReqDTO2.isEmployee_outcome());
            academyCommentRepository2.save(academyCommentEntity2);
            return true;
        }catch (Exception e ){
            log.error("학원 코멘트 등록 실패 : {}", e.getMessage());
            return false;
        }
    }
}
