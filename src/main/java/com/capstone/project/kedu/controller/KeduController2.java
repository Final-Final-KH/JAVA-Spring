package com.capstone.project.kedu.controller;

import com.capstone.project.kedu.dto.KeduResDTO2;
import com.capstone.project.kedu.service.KeduService2;
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
@RequestMapping("/course")
@RequiredArgsConstructor
public class KeduController2 {

    private final KeduService2 keduService2;

    @GetMapping("/list")
    public Map<String, Object> courseList() {
        log.info("Fetching course list...");
        Map<String, Object> resultMap = new HashMap<>();
        List<KeduResDTO2> list = keduService2.findAllCourse();
        log.info("Found {} courses", list.size());
        resultMap.put("list", list);
        return resultMap;
    }

}
