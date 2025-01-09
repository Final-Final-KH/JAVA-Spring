package com.capstone.project.kedu.controller;

import com.capstone.project.kedu.dto.comment.AcademyCommentReqDTO2;
import com.capstone.project.kedu.dto.comment.AcademyCommentResDTO2;
import com.capstone.project.kedu.service.AcademyCommentService2;
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
@RequestMapping("/academy_comment")
@RequiredArgsConstructor
public class AcademyCommentController {
    private final AcademyCommentService2 academyCommentService2;

    @PostMapping("/create")
    public ResponseEntity<Boolean> create (@RequestBody AcademyCommentReqDTO2 academyCommentReqDTO2){
        boolean isSuccess = academyCommentService2.create(academyCommentReqDTO2);
        return ResponseEntity.ok(isSuccess);
    }
}
