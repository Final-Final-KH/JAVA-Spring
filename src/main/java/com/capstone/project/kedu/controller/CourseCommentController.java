package com.capstone.project.kedu.controller;

import com.capstone.project.kedu.service.CourseCommentService2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://192.168.10.25:3000"})  // 두 개의 origin을 추가
@RestController
@RequestMapping("/course_comment")
@RequiredArgsConstructor
public class CourseCommentController {
    private final CourseCommentService2 courseCommentService2;

}
