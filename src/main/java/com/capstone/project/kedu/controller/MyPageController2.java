package com.capstone.project.kedu.controller;

import com.capstone.project.kedu.dto.board.KeduBoardResDTO2;
import com.capstone.project.kedu.dto.comment.AcademyCommentResDTO2;
import com.capstone.project.kedu.dto.comment.CourseCommentResDTO2;
import com.capstone.project.kedu.dto.mypage.SkillHubReqDTO2;
import com.capstone.project.kedu.dto.mypage.SkillHubResDTO2;
import com.capstone.project.kedu.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://192.168.10.25:3000"})  // 두 개의 origin을 추가
@RestController
@RequestMapping("/my_page")
@RequiredArgsConstructor
public class MyPageController2 {
    private final MyPageService2 myPageService2;
    private final KeduBoardService2 keduBoardService2;
    private final MyCourseService2 myCourseService2;
    private final AcademyCommentService2 academyCommentService2;
    private final CourseCommentService2 courseCommentService2;


    @PostMapping("/create")
    public ResponseEntity<Boolean>create (@RequestBody SkillHubReqDTO2 skillHubReqDTO2){
        boolean isSuccess = myPageService2.addSkill(skillHubReqDTO2);
        return ResponseEntity.ok(isSuccess);
    }

    @PostMapping("/update")
    public ResponseEntity<Boolean>update (@RequestBody SkillHubReqDTO2 skillHubReqDTO2,
                                          @RequestParam(value = "id")Long id){
        boolean isSuccess = myPageService2.update(skillHubReqDTO2, id);
        return ResponseEntity.ok(isSuccess);
    }

    @PostMapping("/delete")
    public ResponseEntity<Boolean>delete (@RequestParam(value = "member_id") int memberId,
                                          @RequestParam(value = "id")Long id){
        boolean isSuccess = myPageService2.delete(memberId, id);
        return ResponseEntity.ok(isSuccess);
    }

    @PostMapping("/list")
    public Map<String , Object> list (@RequestParam(value = "member_id")int memberId){
        Map<String , Object> resultMap = new HashMap<>();
        List<SkillHubResDTO2> list = myPageService2.list(memberId);
        resultMap.put("list",list);
        return resultMap;
    }

    // 나의 게시글 조회 // 타이틀만
    @PostMapping("/my_boards")
    public Map<String, Object> myBoardList(@RequestParam(value = "member_id") int member_id){
        Map<String, Object> resultMap = new HashMap<>();
        List<KeduBoardResDTO2> list = keduBoardService2.myBoard(member_id);
        resultMap.put("list",list);
        return resultMap;
    }

    // 나의 학원 코멘트 조회
    @PostMapping("/my_academy_comment")
    public Map<String, Object> myAcademyComment(@RequestParam(value = "member_id") int member_id){
        Map<String, Object> resultMap = new HashMap<>();
        List<AcademyCommentResDTO2> list = academyCommentService2.myAcademyComment(member_id);
        resultMap.put("list",list);
        return resultMap;
    }

    // 나의 강의 코멘트 조회
    @PostMapping("/my_course_comment")
    public Map<String, Object> myCourseComment(@RequestParam(value = "member_id") int member_id){
        Map<String, Object> resultMap = new HashMap<>();
        List<CourseCommentResDTO2> list = courseCommentService2.myCourseComment(member_id);
        resultMap.put("list",list);
        return resultMap;
    }
}
