package com.capstone.project.kedu.controller;

import com.capstone.project.kedu.dto.board.KeduBoardResDTO2;
import com.capstone.project.kedu.dto.edu.request.MyCourseDeleteReqDTO2;
import com.capstone.project.kedu.dto.edu.request.MyCourseReqDTO2;
import com.capstone.project.kedu.dto.edu.response.MyAcademyResDTO2;
import com.capstone.project.kedu.dto.edu.response.MyCourseResDTO2;
import com.capstone.project.kedu.dto.edu.request.MyCourseSearchReqDTO2;
import com.capstone.project.kedu.service.MyCourseService2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://192.168.10.25:3000"})  // 두 개의 origin을 추가
@RestController
@RequestMapping("/my_course")
@RequiredArgsConstructor
public class MyCourseController2 {

    private MyCourseService2 myCourseService2;

    // 생성자 주입
    @Autowired
    public MyCourseController2(MyCourseService2 myCourseService2) {
        this.myCourseService2 = myCourseService2;
    }

    // member_id, course_id, academy_id, academy_name, course_name이 필요함
    @PostMapping("/add_my_course")
    public ResponseEntity<Boolean> add_my_course(@RequestBody MyCourseReqDTO2 myCourseReqDTO2){
        boolean isSuccess = myCourseService2.addMyCourse(myCourseReqDTO2);
        return ResponseEntity.ok(isSuccess);
    }
    // 나의 학원 등록하기
    @PostMapping("/add_my_academy")
    public ResponseEntity<Boolean> add_my_academy(@RequestBody MyCourseReqDTO2 myCourseReqDTO2){
        boolean isSuccess = myCourseService2.addMyAcademy(myCourseReqDTO2);
        return ResponseEntity.ok(isSuccess);
    }
    // course_id와 member_id로 삭제
    @PostMapping("/delete_my_course")
    public ResponseEntity<Boolean> delete_my_course(@RequestBody MyCourseDeleteReqDTO2 myCourseReqDTO2){
        boolean isSuccess = myCourseService2.deleteMyCourse(myCourseReqDTO2);
        return ResponseEntity.ok(isSuccess);
    }
    // member_id로 검색
    @PostMapping("/search_my_course")
    public Map<String, Object> search_my_course(@RequestParam(value = "member_id") int member_id){
        Map<String , Object> resultMap = new HashMap<>();
        List<MyCourseResDTO2> list = myCourseService2.search_my_course(member_id);
        resultMap.put("list",list);
        return resultMap;
    }
    // 나의 학원 조회
    @PostMapping("/my_academy")
    public Map<String, Object> myAcademyList(@RequestParam(value = "member_id") int member_id){
        Map<String, Object> resultMap = new HashMap<>();
        List<MyAcademyResDTO2> list = myCourseService2.myAcademy(member_id);
        resultMap.put("list",list);
        return resultMap;
    }
    // 나의 강의 조회
    @GetMapping("/my_course")
    public ResponseEntity<Map<String, Object>> myCourseList(@RequestParam(value = "member_id") int member_id,
                                                            @RequestParam(value = "academy_id") Long academy_id){
        Map<String, Object> resultMap = new HashMap<>();
        List<MyCourseResDTO2> list = myCourseService2.myCourse(member_id, academy_id);
        resultMap.put("list",list);
        return ResponseEntity.ok(resultMap);
    }
    // 나의 학원 등록 여부 조회
    @GetMapping("/check_academy")
    public ResponseEntity<Boolean> check_academy(
            @RequestParam(value = "academy_id") Long academyId,
            @RequestParam(value = "member_id") int memberId) {
        log.info("체크 시 아카데미 아이디 : {}", academyId);
        log.info("체크 시 멤버 아이디 : {}", memberId);
        boolean isSuccess = myCourseService2.check_academy(academyId, memberId);
        return ResponseEntity.ok(!isSuccess);
    }
    // 나의 강의 등록 여부 확인
    @GetMapping("/check_course")
    public ResponseEntity<Boolean> check_course(
            @RequestParam(value = "course_id") Long courseId,
            @RequestParam(value = "member_id") int member_id){
        boolean isSuccess = myCourseService2.check_course(courseId, member_id);
        return ResponseEntity.ok(isSuccess);
    }



}
