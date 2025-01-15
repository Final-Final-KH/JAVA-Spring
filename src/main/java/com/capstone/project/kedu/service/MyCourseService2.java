package com.capstone.project.kedu.service;

import com.capstone.project.kedu.dto.edu.request.MyCourseDeleteReqDTO2;
import com.capstone.project.kedu.dto.edu.request.MyCourseReqDTO2;
import com.capstone.project.kedu.dto.edu.response.MyAcademyResDTO2;
import com.capstone.project.kedu.dto.edu.response.MyCourseResDTO2;
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

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyCourseService2 {
    private final MemberRepository memberRepository;
    private final CourseRepository2 courseRepository2;
    private final AcademyRepository2 academyRepository2;
    private final MyCourseRepository2 myCourseRepository2;
    @Transactional
    public boolean addMyAcademy(MyCourseReqDTO2 myCourseReqDTO2) {
        try{
            Member member = memberRepository.findById(myCourseReqDTO2.getMember_id())
                    .orElseThrow(()-> new RuntimeException("해당 회원이 존재하지 않습니다."));
            AcademyEntity2 academyEntity2 = academyRepository2.findById(myCourseReqDTO2.getAcademy_id())
                    .orElseThrow(()-> new RuntimeException("해당 학원이 존재하지 않습니다."));
            MyCourseEntity2 myCourseEntity2 = new MyCourseEntity2();
            myCourseEntity2.setMember(member);
            myCourseEntity2.setAcademyEntity2(academyEntity2);
            myCourseEntity2.setAcademy_name(myCourseReqDTO2.getAcademy());
            myCourseRepository2.save(myCourseEntity2);
            return true;
        } catch (Exception e ){
            log.error("나의 학원 목록 등록 실패 : {}", e.getMessage());
            return false;
        }
    }
    // 수강 목록은 오히려 update가 되어야 할수도 있음
    @Transactional
    public boolean addMyCourse(MyCourseReqDTO2 myCourseReqDTO2) {
        try {
            // 동일한 member_id와 academy_id로 수강 목록을 찾되, course_id가 다르면 새로운 수강 기록으로 처리
            MyCourseEntity2 myCourseEntity2 = myCourseRepository2
                    .findByMember_IdAndAcademyEntity2_AcademyIdAndCourseEntity2_CourseId(
                            myCourseReqDTO2.getMember_id(),
                            myCourseReqDTO2.getAcademy_id(),
                            myCourseReqDTO2.getCourse_id()
                    );

            if (myCourseEntity2 == null) { // 기존에 해당 수강 기록이 없으면
                Member member = memberRepository.findById(myCourseReqDTO2.getMember_id())
                        .orElseThrow(() -> new RuntimeException("해당 회원이 존재하지 않습니다."));
                CourseEntity2 courseEntity2 = courseRepository2.findById(myCourseReqDTO2.getCourse_id())
                        .orElseThrow(() -> new RuntimeException("해당 강의가 존재하지 않습니다."));
                AcademyEntity2 academyEntity2 = academyRepository2.findById(myCourseReqDTO2.getAcademy_id())
                        .orElseThrow(() -> new RuntimeException("해당 학원이 존재하지 않습니다."));
                // 새로운 수강 기록을 생성
                myCourseEntity2 = new MyCourseEntity2();
                myCourseEntity2.setMember(member);
                myCourseEntity2.setCourseEntity2(courseEntity2);
                myCourseEntity2.setAcademyEntity2(academyEntity2);
                myCourseEntity2.setCourse_name(myCourseReqDTO2.getCourse());
                myCourseEntity2.setAcademy_name(myCourseReqDTO2.getAcademy());


                // 새로운 수강 기록 저장
                myCourseRepository2.save(myCourseEntity2);
                return true;
            } else {
                // 이미 동일한 member_id, academy_id, course_id로 존재하는 수강 기록이 있으면 업데이트하지 않음
                log.info("이미 동일한 수강 기록이 존재합니다.");
                return false;
            }

        } catch (Exception e) {
            log.error("나의 수강 목록 등록 실패 : {}", e.getMessage());
            return false;
        }
    }


    public boolean deleteMyCourse(MyCourseDeleteReqDTO2 myCourseReqDTO2) {
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

    // 나의 강의 조회
    public List<MyCourseResDTO2> search_my_course(int memberId) {
        List<MyCourseEntity2> myCourseEntity2 =  myCourseRepository2.findByMemberId(memberId);
        return convertEntityToDto(myCourseEntity2);
    }

    // 나의 학원 조회
    public List<MyAcademyResDTO2> myAcademy(int memberId) {
        List<MyCourseEntity2> myCourseEntity2s = myCourseRepository2.findByMemberId(memberId);
        System.out.println("myCourseEntity2s size: " + myCourseEntity2s.size());  // 로그 추가
        return convertAcademyEntityToDto(myCourseEntity2s);
    }
    // 학원 등록 여부 조회
    public boolean check_academy(Long academyId, int memberId) {
        return myCourseRepository2.existsByMember_IdAndAcademyEntity2_AcademyId(memberId, academyId);
    }

    private List<MyAcademyResDTO2> convertAcademyEntityToDto(List<MyCourseEntity2> myCourseEntity2s) {
        List<MyAcademyResDTO2> myAcademyResDTO2s = new ArrayList<>();
        for (MyCourseEntity2 myCourseEntity2 : myCourseEntity2s) {
            MyAcademyResDTO2 myAcademyResDTO2 = new MyAcademyResDTO2();
            myAcademyResDTO2.setMember_id(myCourseEntity2.getMember().getMemberId());
            myAcademyResDTO2.setAcademy(myCourseEntity2.getAcademyEntity2().getAcademyName());
            myAcademyResDTO2.setAcademy_id(myCourseEntity2.getAcademyEntity2().getAcademyId());

            // List에 객체 추가
            myAcademyResDTO2s.add(myAcademyResDTO2);
        }
        return myAcademyResDTO2s;
    }


    public List<MyCourseResDTO2> convertEntityToDto(List<MyCourseEntity2> myCourseEntity){

        List<MyCourseResDTO2> myCourseResDTO = new ArrayList<>();

        for(MyCourseEntity2 myCourseEntity2 : myCourseEntity){
            MyCourseResDTO2 myCourseResDTO2 = new MyCourseResDTO2();

            myCourseResDTO2.setAcademy_id(myCourseEntity2.getAcademyEntity2().getAcademyId());
            myCourseResDTO2.setCourse_id(myCourseEntity2.getCourseEntity2().getCourseId());
            myCourseResDTO.add(myCourseResDTO2);
        }

        return myCourseResDTO;
    }


}
