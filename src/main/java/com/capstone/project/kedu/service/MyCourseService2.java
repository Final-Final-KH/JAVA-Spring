package com.capstone.project.kedu.service;

import com.capstone.project.kedu.dto.edu.MyCourseReqDTO2;
import com.capstone.project.kedu.entity.edu.AcademyEntity2;
import com.capstone.project.kedu.entity.edu.CourseEntity2;
import com.capstone.project.kedu.entity.edu.MyCourseEntity2;
import com.capstone.project.kedu.repository.edu.AcademyRepository2;
import com.capstone.project.kedu.repository.edu.CourseRepository2;
import com.capstone.project.kedu.repository.edu.MyCourseRepository2;
import com.capstone.project.member.entity.Member;
import com.capstone.project.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyCourseService2 {
    private final MemberRepository memberRepository;
    private final CourseRepository2 courseRepository2;
    private final AcademyRepository2 academyRepository2;
    private final MyCourseRepository2 myCourseRepository2;
    
    @Transactional
    public boolean addMyCourse(MyCourseReqDTO2 myCourseReqDTO2) {
        try{
            Member member = memberRepository.findById(myCourseReqDTO2.getMember_id())
                    .orElseThrow(()-> new RuntimeException("해당 회원이 존재하지 않습니다."));
            CourseEntity2 courseEntity2 = courseRepository2.findById(myCourseReqDTO2
                    .getCourse_id()).orElseThrow(()->new RuntimeException("해당 강의가 존재 하지 않습니다."));
            AcademyEntity2 academyEntity2 = academyRepository2.findById(myCourseReqDTO2.getAcademy_id())
                    .orElseThrow(()->new RuntimeException("해당 학원이 존재 하지 않습니다."));
            MyCourseEntity2 myCourseEntity2 = new MyCourseEntity2();
            myCourseEntity2.setMember(member);
            myCourseEntity2.setCourse(myCourseReqDTO2.getCourse());
            myCourseEntity2.setAcademy(myCourseReqDTO2.getAcademy());
            myCourseEntity2.setCourseEntity2(courseEntity2);
            myCourseEntity2.setAcademyEntity2(academyEntity2);
            myCourseRepository2.save(myCourseEntity2);
            return true;
        } catch (Exception e ){
            log.error("나의 수강 목록 등록 실패 : {}", e.getMessage());
            return false;
        }
    }

    public boolean deleteMyCourse(MyCourseReqDTO2 myCourseReqDTO2) {
        try{
            MyCourseEntity2 myCourseEntity2 = myCourseRepository2.findById(myCourseReqDTO2.getList_id())
                    .orElseThrow(()-> new RuntimeException("해당 수강 기록이 존재 하지 않습니다."));
            Member member = memberRepository.findById(myCourseReqDTO2.getMember_id())
                    .orElseThrow(()-> new RuntimeException("해당 회원이 존재 하지 않습니다."));
            myCourseRepository2.deleteByMemberIdAndListId(myCourseEntity2.getList_id(), member.getMemberId());
            return true;
        }catch (Exception e) {
            log.error("게시물 삭제에 실패 했습니다. : {}", e.getMessage());
            return false;
        }
    }
}
