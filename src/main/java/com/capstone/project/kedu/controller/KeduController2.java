package com.capstone.project.kedu.controller;

import com.capstone.project.kedu.dto.edu.response.*;
import com.capstone.project.kedu.service.KeduService2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
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

    @GetMapping("/list/page")
    public ResponseEntity<Map<String, Object>> courseList(@RequestParam(value = "page", defaultValue = "0") int page,
                                                        @RequestParam(value = "size", defaultValue = "5") int size){
        Map<String, Object> resultMap = new HashMap<>();
        List<KeduResDTO2> list = keduService2.getCourseList(page, size);
        resultMap.put("list", list);
        return ResponseEntity.ok(resultMap);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> listBoards(@RequestParam(value = "page", defaultValue = "0") int page,
                                                          @RequestParam(value = "size", defaultValue = "5") int size) {
        // PageRequest 객체 생성
        PageRequest pageRequest = PageRequest.of(page, size);

        // 서비스 로직을 통해 페이지 갯수 가져오기
        Integer pageCnt = keduService2.getBoards(pageRequest);

        // 결과를 담을 Map 생성
        Map<String, Object> resultMap = new HashMap<>();

        // 결과 데이터와 추가 정보를 resultMap에 넣음
        resultMap.put("totalPages", pageCnt);  // 페이지 수
        resultMap.put("currentPage", page);    // 현재 페이지
        resultMap.put("size", size);           // 페이지 크기

        // 성공적인 응답 반환
        return ResponseEntity.ok(resultMap);
    }

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
