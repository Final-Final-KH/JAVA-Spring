package com.capstone.project.kedu.controller;

import com.capstone.project.kedu.dto.mypage.SkillHubReqDTO2;
import com.capstone.project.kedu.dto.mypage.SkillHubResDTO2;
import com.capstone.project.kedu.service.MyPageService2;
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
@RequestMapping("/my_page")
@RequiredArgsConstructor
public class MyPageController2 {
    private final MyPageService2 myPageService2;

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
}
