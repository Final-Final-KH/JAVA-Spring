//package com.capstone.project.kedu.repository.comment;
//
//
//import com.capstone.project.kedu.dto.board.KeduBoardReqDTO2;
//import com.capstone.project.kedu.dto.comment.AcademyCommentReqDTO2;
//import com.capstone.project.kedu.dto.comment.CourseCommentReqDTO2;
//import com.capstone.project.kedu.dto.edu.request.MyCourseReqDTO2;
//import com.capstone.project.kedu.dto.edu.response.CourseDetailResDTO2;
//import com.capstone.project.kedu.dto.survey.SurveyReqDTO2;
//import com.capstone.project.kedu.entity.board.KeduBoardEntity2;
//import com.capstone.project.kedu.entity.comment.AcademyCommentEntity2;
//import com.capstone.project.kedu.entity.comment.CourseCommentEntity2;
//import com.capstone.project.kedu.entity.edu.AcademyEntity2;
//import com.capstone.project.kedu.entity.edu.CourseEntity2;
//import com.capstone.project.kedu.entity.edu.MyCourseEntity2;
//import com.capstone.project.kedu.entity.survey.SurveyEntity2;
//import com.capstone.project.kedu.repository.board.KeduBoardRepository2;
//import com.capstone.project.kedu.repository.edu.AcademyRepository2;
//import com.capstone.project.kedu.repository.edu.CourseRepository2;
//import com.capstone.project.kedu.repository.edu.MyCourseRepository2;
//import com.capstone.project.kedu.repository.survey.SurveyRepository2;
//import com.capstone.project.member.dto.request.MemberRequestDto;
//import com.capstone.project.member.entity.Member;
//import com.capstone.project.member.repository.MemberRepository;
//import lombok.extern.slf4j.Slf4j;
//import lombok.extern.slf4j.XSlf4j;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import java.util.List;
//
//@ExtendWith(SpringExtension.class)
//@SpringBootTest
//@TestPropertySource(locations = "classpath:application-test.properties")
//@Slf4j
//public class CourseCommentRepository2Test2 {
//    @Autowired
//    private CourseCommentRepository2 courseCommentRepository2;
//
//    @Autowired
//    private MemberRepository memberRepository;
//
//    @Autowired
//    private AcademyRepository2 academyRepository2;
//
//    @Autowired
//    private CourseRepository2 courseRepository2;
//
//    @Autowired
//    private MyCourseRepository2 myCourseRepository2;
//
//    @Autowired
//    private AcademyCommentRepository2 academyCommentRepository2;
//
//   @Autowired
//   private KeduBoardRepository2 keduBoardRepository2;
//
//   @Autowired
//   private SurveyRepository2 surveyRepository2;
//
//    @Test
//    @DisplayName("조인으로 강의 상세 출력 (종합 테스트)")
//    public void 조인으로_강의_상세_출력_종합_테스트(){
//        // 첫번 째 회원
//        MemberRequestDto memberRequestDto = new MemberRequestDto();
//        memberRequestDto.setEmail("agape@naver.com");
//        memberRequestDto.setPassword("agape99");
//        memberRequestDto.setName("김요한");
//        memberRequestDto.setPhoneNumber("123123");
//        memberRequestDto.setShowCompany(false);
//        memberRequestDto.setCurrentCompany("네이버");
//
//        Member member = new Member();
//        member.setEmail(memberRequestDto.getEmail());
//        member.setPassword(memberRequestDto.getPassword());
//        member.setName(memberRequestDto.getName());
//        member.setPhoneNumber(memberRequestDto.getPhoneNumber());
//        member.setShowCompany(memberRequestDto.getShowCompany());
//        member.setCurrentCompany(memberRequestDto.getCurrentCompany());
//        memberRepository.save(member);
//
//        // 두번째 회원
//        MemberRequestDto memberRequestDto1 = new MemberRequestDto();
//        memberRequestDto1.setEmail("faith@naver.com");
//        memberRequestDto1.setPassword("faith99");
//        memberRequestDto1.setPhoneNumber("123");
//        Member member1 = new Member();
//
//        member1.setEmail(memberRequestDto1.getEmail());
//        member1.setPassword(memberRequestDto1.getPassword());
//        member1.setName(memberRequestDto.getName());
//        member1.setPhoneNumber(memberRequestDto1.getPhoneNumber());
//        member1.setShowCompany(memberRequestDto.getShowCompany());
//        member1.setCurrentCompany(memberRequestDto.getCurrentCompany());
//        memberRepository.save(member1);
//
//        AcademyEntity2 academyEntity2 = academyRepository2.findById(1L)
//                .orElseThrow(()-> new RuntimeException("해당 학원 없음"));
//        CourseEntity2 courseEntity2 = courseRepository2.findById(1L)
//                .orElseThrow(()-> new RuntimeException("해당 강의 없음"));
//        Member member2 = memberRepository.findById(1)
//                .orElseThrow(()-> new RuntimeException("해당 회원 없음"));
//        Member member3 = memberRepository.findById(2)
//                .orElseThrow(()-> new RuntimeException("해당 회원 없음"));
//
//        MyCourseEntity2 myCourseEntity2 = new MyCourseEntity2();
//        myCourseEntity2.setAcademyEntity2(academyEntity2);
//        myCourseEntity2.setCourseEntity2(courseEntity2);
//        myCourseEntity2.setMember(member2);
//
//        myCourseRepository2.save(myCourseEntity2);
//
//        MyCourseEntity2 myCourseEntity3 = new MyCourseEntity2();
//        myCourseEntity3.setAcademyEntity2(academyEntity2);
//        myCourseEntity3.setCourseEntity2(courseEntity2);
//        myCourseEntity3.setMember(member3);
//
//        myCourseRepository2.save(myCourseEntity3);
//
//        //학원리뷰 등록
//        AcademyCommentReqDTO2 academyCommentReqDTO2 = new AcademyCommentReqDTO2();
//        academyCommentReqDTO2.setJob(99);
//        academyCommentReqDTO2.setBooks(100);
//        academyCommentReqDTO2.setLecture(88);
//        academyCommentReqDTO2.setFacilities(77);
//        academyCommentReqDTO2.setService(98);
//        academyCommentReqDTO2.setTeacher(78);
//
//        AcademyCommentReqDTO2 academyCommentReqDTO3 = new AcademyCommentReqDTO2();
//        academyCommentReqDTO3.setJob(100);
//        academyCommentReqDTO3.setBooks(100);
//        academyCommentReqDTO3.setLecture(100);
//        academyCommentReqDTO3.setFacilities(100);
//        academyCommentReqDTO3.setService(100);
//        academyCommentReqDTO3.setTeacher(100);
//
//        AcademyCommentEntity2 academyCommentEntity2 = new AcademyCommentEntity2();
//        academyCommentEntity2.setAcademyEntity2(academyEntity2);
//        academyCommentEntity2.setMember(member2);
//        academyCommentEntity2.setJob(academyCommentReqDTO2.getJob());
//        academyCommentEntity2.setBooks(academyCommentReqDTO2.getBooks());
//        academyCommentEntity2.setLecture(academyCommentReqDTO2.getLecture());
//        academyCommentEntity2.setFacilities(academyCommentReqDTO2.getFacilities());
//        academyCommentEntity2.setService(academyCommentReqDTO2.getService());
//        academyCommentEntity2.setTeacher(academyCommentReqDTO2.getTeacher());
//        academyCommentRepository2.save(academyCommentEntity2);
//
//        AcademyCommentEntity2 academyCommentEntity3 = new AcademyCommentEntity2();
//        academyCommentEntity3.setAcademyEntity2(academyEntity2);
//        academyCommentEntity3.setMember(member3);
//        academyCommentEntity3.setJob(academyCommentReqDTO3.getJob());
//        academyCommentEntity3.setBooks(academyCommentReqDTO3.getBooks());
//        academyCommentEntity3.setLecture(academyCommentReqDTO3.getLecture());
//        academyCommentEntity3.setFacilities(academyCommentReqDTO3.getFacilities());
//        academyCommentEntity3.setService(academyCommentReqDTO3.getService());
//        academyCommentEntity3.setTeacher(academyCommentReqDTO3.getTeacher());
//        academyCommentRepository2.save(academyCommentEntity3);
//
//
//        //강의리뷰 등록
//        CourseCommentReqDTO2 courseCommentReqDTO2 = new CourseCommentReqDTO2();
//        courseCommentReqDTO2.setJob(100);
//        courseCommentReqDTO2.setLecture(100);
//        courseCommentReqDTO2.setBooks(100);
//        courseCommentReqDTO2.setTeacher(100);
//        courseCommentReqDTO2.setNewTech(100);
//        courseCommentReqDTO2.setSkillUp(100);
//
//        CourseCommentEntity2 courseCommentEntity2 = new CourseCommentEntity2();
//        courseCommentEntity2.setAcademyEntity2(academyEntity2);
//        courseCommentEntity2.setCourseEntity2(courseEntity2);
//        courseCommentEntity2.setMember(member2);
//        courseCommentEntity2.setJob(courseCommentReqDTO2.getJob());
//        courseCommentEntity2.setLecture(courseCommentReqDTO2.getLecture());
//        courseCommentEntity2.setBooks(courseCommentReqDTO2.getBooks());
//        courseCommentEntity2.setTeacher(courseCommentReqDTO2.getTeacher());
//        courseCommentEntity2.setNewTech(courseCommentReqDTO2.getNewTech());
//        courseCommentEntity2.setSkillUp(courseCommentReqDTO2.getSkillUp());
//        courseCommentRepository2.save(courseCommentEntity2);
//        CourseCommentEntity2 courseCommentEntity3 = new CourseCommentEntity2();
//        courseCommentEntity3.setMember(member3);
//        courseCommentEntity3.setCourseEntity2(courseEntity2);
//        courseCommentEntity3.setAcademyEntity2(academyEntity2);
//        courseCommentRepository2.save(courseCommentEntity3);
//
//        //한줄 코멘트 등록
//        KeduBoardEntity2 keduBoardEntity2 = new KeduBoardEntity2();
//        keduBoardEntity2.setAcademyEntity2(academyEntity2);
//        keduBoardEntity2.setCourseEntity2(courseEntity2);
//        keduBoardEntity2.setMember(member2);
//        keduBoardEntity2.setTitle("GOOD");
//        keduBoardEntity2.setContent("아주 좋아요");
//        KeduBoardEntity2 keduBoardEntity3 = new KeduBoardEntity2();
//        keduBoardEntity3.setAcademyEntity2(academyEntity2);
//        keduBoardEntity3.setCourseEntity2(courseEntity2);
//        keduBoardEntity3.setMember(member3);
//        keduBoardEntity3.setTitle("GOOD");
//        keduBoardEntity3.setContent("아주 좋아습니다.");
//        keduBoardRepository2.save(keduBoardEntity2);
//        keduBoardRepository2.save(keduBoardEntity3);
//        //설문조사 등록
//        SurveyReqDTO2 surveyReqDTO2 = new SurveyReqDTO2();
//        surveyReqDTO2.setLecture("강의 스라바시");
//        surveyReqDTO2.setComment("아주 좋아요");
//        surveyReqDTO2.setTeacher("강사님 좋아요");
//        surveyReqDTO2.setFacilities("시설도 좋아요");
//
//        SurveyEntity2 surveyEntity2 = new SurveyEntity2();
//        surveyEntity2.setAcademyEntity2(academyEntity2);
//        surveyEntity2.setCourseEntity2(courseEntity2);
//        surveyEntity2.setMember(member2);
//        surveyEntity2.setFacilities(surveyReqDTO2.getFacilities());
//        surveyEntity2.setTeacher(surveyReqDTO2.getTeacher());
//        surveyEntity2.setComment(surveyReqDTO2.getComment());
//        surveyEntity2.setLecture(surveyReqDTO2.getLecture());
//        surveyRepository2.save(surveyEntity2);
//        SurveyEntity2 surveyEntity3 = new SurveyEntity2();
//        surveyEntity3.setAcademyEntity2(academyEntity2);
//        surveyEntity3.setCourseEntity2(courseEntity2);
//        surveyEntity3.setMember(member3);
//        surveyEntity3.setFacilities(surveyReqDTO2.getFacilities());
//        surveyEntity3.setTeacher(surveyReqDTO2.getTeacher());
//        surveyEntity3.setComment(surveyReqDTO2.getComment());
//        surveyEntity3.setLecture(surveyReqDTO2.getLecture());
//        surveyRepository2.save(surveyEntity3);
//
//        List<CourseDetailResDTO2> list = courseCommentRepository2.findReview(1L,1L);
//        for(CourseDetailResDTO2 courseDetailResDTO2 : list){
//            log.info("강의 상세야 출력되어라 : {}", courseDetailResDTO2);
//        }
//    }
//}
