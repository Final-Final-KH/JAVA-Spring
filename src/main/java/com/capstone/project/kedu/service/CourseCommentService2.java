package com.capstone.project.kedu.service;

import com.capstone.project.kedu.dto.comment.CourseCommentReqDTO2;
import com.capstone.project.kedu.dto.comment.CourseCommentResDTO2;
import com.capstone.project.kedu.entity.comment.CourseCommentEntity2;
import com.capstone.project.kedu.entity.edu.CourseEntity2;
import com.capstone.project.kedu.repository.comment.CourseCommentRepository2;
import com.capstone.project.kedu.repository.edu.CourseRepository2;
import com.capstone.project.member.entity.Member;
import com.capstone.project.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CourseCommentService2 {
    private final CourseCommentRepository2 courseCommentRepository2;
    private final MemberRepository memberRepository;
    private final CourseRepository2 courseRepository2;
    public CourseCommentService2(CourseCommentRepository2 courseCommentRepository2, MemberRepository memberRepository, CourseRepository2 courseRepository2) {
        this.courseCommentRepository2 = courseCommentRepository2;
        this.memberRepository = memberRepository;
        this.courseRepository2 = courseRepository2;
    }

    @Transactional
    public boolean create(CourseCommentReqDTO2 courseCommentReqDTO2) {
        try{
            Member member = memberRepository.findById(courseCommentReqDTO2.getMember_id())
                    .orElseThrow(()-> new RuntimeException("해당 회원이 존재하지 않습니다."));
            CourseEntity2 courseEntity2 = courseRepository2.findById(courseCommentReqDTO2.getCourse_id())
                    .orElseThrow(()-> new RuntimeException("해당 강의는 존재하지 않습니다."));
            CourseCommentEntity2 courseCommentEntity2 = new CourseCommentEntity2();
            courseCommentEntity2.setCourseEntity2(courseEntity2);
            courseCommentEntity2.setMember(member);
            courseCommentEntity2.setJob(courseCommentReqDTO2.getJob());
            courseCommentEntity2.setBooks(courseCommentReqDTO2.getBooks());
            courseCommentEntity2.setLecture(courseCommentReqDTO2.getLecture());
            courseCommentEntity2.setEmployee_outcome(courseCommentReqDTO2.isEmployee_outcome());
            courseCommentEntity2.setNewTech(courseCommentReqDTO2.getNewTech());
            courseCommentEntity2.setSkillUp(courseCommentReqDTO2.getSkillUp());
            courseCommentEntity2.setTeacher(courseCommentReqDTO2.getTeacher());
            courseCommentRepository2.save(courseCommentEntity2);
            return true;
        }catch (Exception e ){
            log.error("강의 평가 등록 실패 : {}", e.getMessage());
            return false;
        }
    }

    public List<CourseCommentResDTO2> list(long courseId) {
        List<CourseCommentEntity2> courseCommentEntity2s =
                courseCommentRepository2.findAllByCourseEntity2_CourseId(courseId);
        List<CourseCommentResDTO2> courseCommentResDTO2List = new ArrayList<>();
        for(CourseCommentEntity2 courseCommentEntity2 : courseCommentEntity2s){
            courseCommentResDTO2List.add(convertEntityToDTO(courseCommentEntity2));
        }
        return courseCommentResDTO2List;
    }
    public boolean delete(Long id, int memberId) {
        try{
            CourseCommentEntity2 courseCommentEntity2 = courseCommentRepository2.findById(id)
                    .orElseThrow(()-> new RuntimeException("해당 강의 평가가 존재하지 않습니다."));
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(()-> new RuntimeException("해당 회원은 존재하지 않습니다."));
            if(courseCommentEntity2.getMember().getMemberId().equals(memberId)){
                courseCommentRepository2.delete(courseCommentEntity2);
                return true;
            }else {
                log.error("게시글은 작성자만 삭제 할수 있습니다.");
                return false;
            }
        }catch (Exception e) {
            log.error("게시글 삭제 실패");
            return false;
        }
    }
    public boolean update(CourseCommentReqDTO2 courseCommentReqDTO2, Long id) {
        try{
            CourseCommentEntity2 courseCommentEntity2 = courseCommentRepository2.findById(id)
                    .orElseThrow(()-> new RuntimeException("해당 강의 평가가 존재하지 않습니다."));
            Member member = memberRepository.findById(courseCommentReqDTO2.getMember_id())
                    .orElseThrow(()-> new RuntimeException("해당 회원은 존재하지 않습니다."));
            if(courseCommentEntity2.getMember().getMemberId().equals(courseCommentReqDTO2.getMember_id())){
                courseCommentEntity2.setSkillUp(courseCommentReqDTO2.getSkillUp());
                courseCommentEntity2.setBooks(courseCommentReqDTO2.getBooks());
                courseCommentEntity2.setJob(courseCommentReqDTO2.getJob());
                courseCommentEntity2.setTeacher(courseCommentReqDTO2.getTeacher());
                courseCommentEntity2.setLecture(courseCommentReqDTO2.getLecture());
                courseCommentEntity2.setNewTech(courseCommentReqDTO2.getNewTech());
                courseCommentEntity2.setEmployee_outcome(courseCommentReqDTO2.isEmployee_outcome());
                courseCommentRepository2.save(courseCommentEntity2);
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

    private CourseCommentResDTO2 convertEntityToDTO(CourseCommentEntity2 courseCommentEntity2) {
        CourseCommentResDTO2 courseCommentResDTO2 = new CourseCommentResDTO2();
        courseCommentResDTO2.setCourse_comment_id(courseCommentEntity2.getCourse_comment_id());
        courseCommentResDTO2.setCourse_id(courseCommentEntity2.getCourseEntity2().getCourseId());
        courseCommentResDTO2.setTeacher(courseCommentEntity2.getTeacher());
        courseCommentResDTO2.setJob(courseCommentEntity2.getJob());
        courseCommentResDTO2.setBooks(courseCommentEntity2.getBooks());
        courseCommentResDTO2.setLecture(courseCommentEntity2.getLecture());
        courseCommentResDTO2.setMember_id(courseCommentEntity2.getMember().getMemberId());
        courseCommentResDTO2.setNewTech(courseCommentEntity2.getNewTech());
        courseCommentResDTO2.setSkillUp(courseCommentEntity2.getSkillUp());
        courseCommentResDTO2.setEmployee_outcome(courseCommentEntity2.isEmployee_outcome());
        return courseCommentResDTO2;
    }
}
