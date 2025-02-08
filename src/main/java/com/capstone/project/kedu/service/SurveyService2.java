package com.capstone.project.kedu.service;

import com.capstone.project.kedu.dto.survey.SurveyReqDTO2;
import com.capstone.project.kedu.dto.survey.SurveyResDTO2;
import com.capstone.project.kedu.entity.edu.AcademyEntity2;
import com.capstone.project.kedu.entity.edu.CourseEntity2;
import com.capstone.project.kedu.entity.survey.SurveyEntity2;
import com.capstone.project.kedu.repository.edu.AcademyRepository2;
import com.capstone.project.kedu.repository.edu.CourseRepository2;
import com.capstone.project.kedu.repository.survey.SurveyRepository2;
import com.capstone.project.member.entity.Member;
import com.capstone.project.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SurveyService2 {
    private final MemberRepository memberRepository;
    private final AcademyRepository2 academyRepository2;
    private final SurveyRepository2 surveyRepository2;
    private final CourseRepository2 courseRepository2;
    public SurveyService2(MemberRepository memberRepository, AcademyRepository2 academyRepository2, SurveyRepository2 surveyRepository2, CourseRepository2 courseRepository2) {
        this.memberRepository = memberRepository;
        this.academyRepository2 = academyRepository2;
        this.surveyRepository2 = surveyRepository2;
        this.courseRepository2 = courseRepository2;
    }
    @Transactional
    public boolean create(SurveyReqDTO2 surveyReqDTO2) {
        try{
            Member member = memberRepository.findById(surveyReqDTO2.getMember_id())
                    .orElseThrow(()-> new RuntimeException("해당 회원이 존재하지 않습니다."));
            AcademyEntity2 academyEntity2 = academyRepository2.findById(surveyReqDTO2.getAcademy_id())
                    .orElseThrow(()-> new RuntimeException("해당 학원이 존재하지 않습니다."));
            CourseEntity2 courseEntity2 = courseRepository2.findById(surveyReqDTO2.getCourse_id())
                    .orElseThrow(()-> new RuntimeException("해당 강의가 존재하지 않습니다."));
            SurveyEntity2 surveyEntity2 = new SurveyEntity2();
            surveyEntity2.setComment(surveyReqDTO2.getComment());
            surveyEntity2.setTeacher(surveyReqDTO2.getTeacher());
            surveyEntity2.setFacilities(surveyReqDTO2.getFacilities());
            surveyEntity2.setLecture(surveyReqDTO2.getLecture());
            surveyEntity2.setCourseEntity2(courseEntity2);
            surveyEntity2.setAcademyEntity2(academyEntity2);
            surveyEntity2.setMember(member);
            surveyRepository2.save(surveyEntity2);
            return true;
        }catch (Exception e ){
            log.error("게시글 등록 실패 : {}", e.getMessage());
            return false;
        }
    }

    public boolean update(SurveyReqDTO2 surveyReqDTO2, Long survey_id) {
        try{
            AcademyEntity2 academyEntity2 = academyRepository2.findById(surveyReqDTO2.getAcademy_id())
                    .orElseThrow(()-> new RuntimeException("해당 학원이 존재하지 않습니다."));
            Member member = memberRepository.findById(surveyReqDTO2.getMember_id())
                    .orElseThrow(()-> new RuntimeException("해당 회원이 존재하지 않습니다."));
            SurveyEntity2 surveyEntity2 = surveyRepository2.findById(survey_id)
                    .orElseThrow(()-> new RuntimeException("해당 설문조사가 존재하지 않습니다."));
            CourseEntity2 courseEntity2 = courseRepository2.findById(surveyReqDTO2.getCourse_id())
                    .orElseThrow(()-> new RuntimeException("해당 강의는 존재하지 않습니다."));
            if(surveyEntity2.getMember().getMemberId().equals(surveyReqDTO2.getMember_id())){
                surveyEntity2.setLecture(surveyReqDTO2.getLecture());
                surveyEntity2.setFacilities(surveyReqDTO2.getFacilities());
                surveyEntity2.setTeacher(surveyReqDTO2.getTeacher());
                surveyEntity2.setComment(surveyReqDTO2.getComment());
                surveyRepository2.save(surveyEntity2);
                return true;
            }else {
                log.error("게시글은 작성자만 수정 할수 있습니다.");
                return false;
            }
        }catch (Exception e) {
            log.error("게시글 수정 실패");
            return false;
        }
    }

    public boolean delete(Long surveyId, int memberId) {
        try{
            SurveyEntity2 surveyEntity2 = surveyRepository2.findById(surveyId)
                    .orElseThrow(()-> new RuntimeException("해당 게시물이 존재하지 않습니다."));
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(()-> new RuntimeException("해당 회원이 존재하지 않습니다."));

            if(!surveyEntity2.getMember().getMemberId().equals(memberId)){
                throw new RuntimeException("게시물 삭제 권한이 없습니다.");
            }
            surveyRepository2.delete(surveyEntity2);
            return true;
        }catch (Exception e) {
            log.error("게시물 삭제에 실패 했습니다. : {}", e.getMessage());
            return false;
        }
    }

    public List<SurveyResDTO2> list(Long academyId, Long course_id) {
        List<SurveyEntity2>surveyEntity2 = surveyRepository2
                .findByAcademyEntity2AcademyIdAndCourseEntity2CourseId(academyId,course_id);
        List<SurveyResDTO2> surveyResDTO2List = new ArrayList<>();
        for(SurveyEntity2 surveyEntity : surveyEntity2){
            surveyResDTO2List.add(convertEntityToDTO(surveyEntity));
        }
        return surveyResDTO2List;
    }

    private SurveyResDTO2 convertEntityToDTO(SurveyEntity2 surveyEntity) {
        SurveyResDTO2 surveyResDTO2 = new SurveyResDTO2();
        surveyResDTO2.setSurvey_id(surveyEntity.getSurvey_id());
        surveyResDTO2.setMember_id(surveyEntity.getMember().getMemberId());
        surveyResDTO2.setAcademy_id(surveyEntity.getAcademyEntity2().getAcademyId());
        surveyResDTO2.setCourse_id(surveyEntity.getCourseEntity2().getCourseId());
        surveyResDTO2.setFacilities(surveyEntity.getFacilities());
        surveyResDTO2.setComment(surveyEntity.getComment());
        surveyResDTO2.setLecture(surveyEntity.getLecture());
        surveyResDTO2.setTeacher(surveyEntity.getTeacher());
        return surveyResDTO2;
    }
}
