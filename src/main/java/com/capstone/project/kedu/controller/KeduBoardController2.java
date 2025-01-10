package com.capstone.project.kedu.controller;

import com.capstone.project.kedu.dto.board.*;
import com.capstone.project.kedu.service.KeduBoardService2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://192.168.10.25:3000"})  // 두 개의 origin을 추가
@RestController
@RequestMapping("/kedu_board")
@RequiredArgsConstructor
public class KeduBoardController2 {

    private KeduBoardService2 keduBoardService2;

    @Autowired  // 생성자 주입
    public KeduBoardController2(KeduBoardService2 keduBoardService2) {
        this.keduBoardService2 = keduBoardService2;
    }

    // 게시글 전체 조회  academy_id 와 course_id 필요
    @PostMapping("/list")
    public Map<String , Object> boardList(@RequestBody KeduBoardReqDTO2 keduBoardReqDTO2) {
        Map<String, Object> resultMap = new HashMap<>();
        List<KeduBoardResDTO2> list = keduBoardService2.board(keduBoardReqDTO2);
        resultMap.put("list",list);
        return resultMap;
    }

    // 게시글 상세 조회  id 로 조회
    @PostMapping("/detail/{id}")
    public Map<String , Object> detail_board(@RequestBody KeduBoardDetailReqDTO2 keduBoardDetailReqDTO2){
        Map<String, Object> resultMap = new HashMap<>();
        KeduBoardDetailResDTO2 list = keduBoardService2.boardDetail(keduBoardDetailReqDTO2.getId());
        resultMap.put("list",list);
        return resultMap;
    }

    // 게시글 등록
    // title, content, academy_id, course_id, regDate, user_id, member_id
    @PostMapping("/new")
    public ResponseEntity<Boolean> boardRegister(@RequestBody KeduBoardRegReqDTO2 keduBoardRegReqDTO2){
        boolean isSuccess = keduBoardService2.newBoard(keduBoardRegReqDTO2);
        return ResponseEntity.ok(isSuccess);
    }

    // 게시글 삭제
    @PostMapping("/delete")
    public ResponseEntity<Boolean> boardDelete(@RequestParam(value = "member_id") int member_id, @RequestParam(value = "id") Long id){
        boolean isSuccess = keduBoardService2.deleteBoard(member_id, id);
        return ResponseEntity.ok(isSuccess);
    }

    // 게시글 업데이트
    @PostMapping("/update")
    public ResponseEntity<Boolean> boardUpdate(@RequestBody KeduBoardUpdateReqDTO2 keduBoardUpdateReqDTO2){
        boolean isSuccess = keduBoardService2.updateBoard(keduBoardUpdateReqDTO2);
        return ResponseEntity.ok(isSuccess);
    }
}
