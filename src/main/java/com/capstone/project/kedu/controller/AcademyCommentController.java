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
import java.util.Optional;

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

    // 컬럼별 평균
    @GetMapping("/sub_total_avg")
    public ResponseEntity<Map<String, Object>> sub_total_avg(@RequestParam(value = "academy_id") Long academy_id){
        Map<String, Object> resultMap = new HashMap<>();
        AcademyCommentResDTO2 sub_total_avg = academyCommentService2.sub_total_avg(academy_id);
        resultMap.put("sub_total_avg",sub_total_avg);
        return ResponseEntity.ok(resultMap);
    }
    // 취업률
    @GetMapping("/empl")
    public ResponseEntity<Map<String, Object>> empl(@RequestParam(value = "academy_id") Long academy_id){
        Map<String, Object> resultMap = new HashMap<>();
        Optional<Integer> empl = academyCommentService2.empl(academy_id);
        resultMap.put("empl",empl);
        return ResponseEntity.ok(resultMap);
    }

    @PostMapping("/list")
    public Map<String, Object> list (@RequestParam(value = "academy_id") Long id){
        Map<String, Object> resultMap = new HashMap<>();
        List<AcademyCommentResDTO2> list = academyCommentService2.list(id);
        resultMap.put("list", list);
        return resultMap;
    }

    @PostMapping("/update")
    public ResponseEntity<Boolean> update (@RequestBody AcademyCommentReqDTO2 academyCommentReqDTO2,
                                           @RequestParam(value = "academy_comment_id") Long id){
        boolean isSuccess = academyCommentService2.update(academyCommentReqDTO2,id);
        return ResponseEntity.ok(isSuccess);
    }

    @PostMapping("/delete")
    public ResponseEntity<Boolean> delete(@RequestParam(value = "academy_comment_id") Long comment_id,
                                          @RequestParam(value = "member_id") int memberId){
        boolean isSuccess = academyCommentService2.delete(comment_id, memberId);
        return ResponseEntity.ok(isSuccess);
    }
}
