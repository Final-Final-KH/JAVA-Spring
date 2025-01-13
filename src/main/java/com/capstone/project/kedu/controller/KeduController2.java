package com.capstone.project.kedu.controller;

import com.capstone.project.kedu.dto.edu.response.*;
import com.capstone.project.kedu.service.KeduService2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @GetMapping("/academy")
    public Map<String, Object> academyList() {
        Map<String, Object> resultMap = new HashMap<>();
        List<AcademyResDTO2> list = keduService2.findAllAcademy();
        resultMap.put("list", list);
        return resultMap;
    }

    @GetMapping("/region")
    public Map<String, Object> region(){
        Map<String, Object> resultMap = new HashMap<>();
        List<RegionResDTO2> list = keduService2.findAllRegion();
        resultMap.put("list", list);
        return resultMap;
    }

    @GetMapping("/district")
    public Map<String , Object> district(@RequestParam(value = "region_name") String region){
        Map<String, Object> resultMap = new HashMap<>();
        List<DistrictResDTO2> list = keduService2.findByRegionDistrict(region);
        resultMap.put("list", list);
        return resultMap;
    }

    @GetMapping("/academy_list")
    public Map<String , Object> academy(@RequestParam(value = "region") String region){
        log.info("지역 정보를 잘 가져오는지 : {}", region);
        Map<String, Object> resultMap = new HashMap<>();
        List<AcademyResDTO2> list = keduService2.findAcadey(region);
        resultMap.put("list", list);
        return resultMap;

    }

    @GetMapping("/lecture")
    public Map<String, Object> lecture(@RequestParam(value = "region")String region, @RequestParam(value = "academy")String academy){
        Map<String, Object> resultMap = new HashMap<>();
        List<LectureResDTO2> list = keduService2.findLecture(region, academy);
        resultMap.put("list", list);
        return resultMap;
    }

}
