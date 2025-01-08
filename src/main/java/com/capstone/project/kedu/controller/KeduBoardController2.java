package com.capstone.project.kedu.controller;

import com.capstone.project.kedu.dto.board.KeduBoardResDTO2;
import com.capstone.project.kedu.service.KeduBoardService2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://192.168.10.25:3000"})  // 두 개의 origin을 추가
@RestController
@RequestMapping("/kedu_board")
@RequiredArgsConstructor
public class KeduBoardController2 {

    private KeduBoardService2 keduBoardService2;

//    @GetMapping("/list")
//    public Map<String , Object> boardList() {
//        Map<String, Object> resultMap = new HashMap<>();
//        List<KeduBoardResDTO2> list = keduBoardService2.board();
//        resultMap.put("list",list);
//        return resultMap;
//    }
}
