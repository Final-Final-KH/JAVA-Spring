package com.capstone.project.kedu.controller;

import com.capstone.project.kedu.dto.edu.MyCourseReqDTO2;
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

    @PostMapping("/delete_my_course")
    public ResponseEntity<Boolean> delete_my_course(@RequestBody MyCourseReqDTO2 myCourseReqDTO2){
        boolean isSuccess = myCourseService2.deleteMyCourse(myCourseReqDTO2);
        return ResponseEntity.ok(isSuccess);
    }
}
