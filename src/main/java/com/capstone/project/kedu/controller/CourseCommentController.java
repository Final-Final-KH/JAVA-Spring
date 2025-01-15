package com.capstone.project.kedu.controller;

import com.capstone.project.kedu.dto.comment.AcademyCommentResDTO2;
import com.capstone.project.kedu.dto.comment.CourseCommentReqDTO2;
import com.capstone.project.kedu.dto.comment.CourseCommentResDTO2;
import com.capstone.project.kedu.service.CourseCommentService2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://192.168.10.25:3000"})  // 두 개의 origin을 추가
@RestController
@RequestMapping("/course_comment")
@RequiredArgsConstructor
public class CourseCommentController {
    private final CourseCommentService2 courseCommentService2;

    @PostMapping("/create")
    public ResponseEntity<Boolean> create (@RequestBody CourseCommentReqDTO2 courseCommentReqDTO2){
        boolean isSuccess = courseCommentService2.create(courseCommentReqDTO2);
        return ResponseEntity.ok(isSuccess);
    }

    @PostMapping("/list")
    public Map<String,Object> list(@RequestParam(name = "course_id") long course_id){
        Map<String, Object> resultMap = new HashMap<>();
        List<CourseCommentResDTO2> list = courseCommentService2.list(course_id);
        resultMap.put("list", list);
        return resultMap;
    }

    @PostMapping("/update") // 업데이트도 다 member 확인 하고 지우도록 ?
    public ResponseEntity<Boolean> update(@RequestBody CourseCommentReqDTO2 courseCommentReqDTO2,
                                          @RequestParam(value = "course_comment_id")Long id){
        boolean isSuccess = courseCommentService2.update(courseCommentReqDTO2, id);
        return ResponseEntity.ok(isSuccess);
    }

    @PostMapping("/delete")
    public ResponseEntity<Boolean> delete(@RequestParam(value = "course_comment_id") Long id,
                                          @RequestParam(value = "member_id")int memberId){
        boolean isSuccess = courseCommentService2.delete(id,memberId);
        return ResponseEntity.ok(isSuccess);
    }
}
