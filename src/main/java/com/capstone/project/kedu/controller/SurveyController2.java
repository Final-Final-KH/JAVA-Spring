package com.capstone.project.kedu.controller;

import com.capstone.project.kedu.dto.survey.SurveyReqDTO2;
import com.capstone.project.kedu.dto.survey.SurveyResDTO2;
import com.capstone.project.kedu.service.SurveyService2;
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
@RequestMapping("/survey")
@RequiredArgsConstructor
public class SurveyController2 {
    private final SurveyService2 surveyService2;

    @PostMapping("/create")
    public ResponseEntity<Boolean> create(@RequestBody SurveyReqDTO2 surveyReqDTO2){
        boolean isSuccess = surveyService2.create(surveyReqDTO2);
        return ResponseEntity.ok(isSuccess);
    }

    @PostMapping("/update")
    public ResponseEntity<Boolean>update(@RequestBody SurveyReqDTO2 surveyReqDTO2,
                                         @RequestParam(value = "survey_id") Long survey_id
                                        ){
        boolean isSuccess = surveyService2.update(surveyReqDTO2, survey_id);
        return ResponseEntity.ok(isSuccess);
    }

    @PostMapping("/delete")
    public ResponseEntity<Boolean> delete(@RequestParam(value = "survey_id") Long survey_id,
                                          @RequestParam(value = "member_id") int member_id){
        boolean isSuccess = surveyService2.delete(survey_id, member_id);
        return ResponseEntity.ok(isSuccess);
    }

    @PostMapping("/list")
    public Map<String, Object> list (@RequestParam(value = "academy_id")Long academy_id,
                                     @RequestParam(value = "course_id")Long course_id){
        Map<String, Object> resultMap = new HashMap<>();
        List<SurveyResDTO2> list = surveyService2.list(academy_id,course_id);
        resultMap.put("list",list);
        return resultMap;
    }
}
