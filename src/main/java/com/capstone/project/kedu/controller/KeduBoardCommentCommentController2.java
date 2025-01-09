package com.capstone.project.kedu.controller;

import com.capstone.project.kedu.dto.board.KeduBoardCommentCommentReqDTO2;
import com.capstone.project.kedu.dto.board.KeduBoardCommentCommentResDTO2;
import com.capstone.project.kedu.dto.board.KeduBoardCommentResDTO2;
import com.capstone.project.kedu.entity.board.KeduBoardCommentsCommentsEntity2;
import com.capstone.project.kedu.service.KeduBoardService2;
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
@RequestMapping("/board_comment_comment")
@RequiredArgsConstructor
public class KeduBoardCommentCommentController2 {
    private final KeduBoardService2 keduBoardService2;

    @GetMapping("/list")
    public Map<String, Object> list(@RequestParam(value = "id") Long id){
        Map<String, Object> resultMap = new HashMap<>();
        List<KeduBoardCommentCommentResDTO2> list = keduBoardService2.findCommentComment(id);
        resultMap.put("list", list);
        return resultMap;
    }

    @PostMapping("/create")
    public ResponseEntity<Boolean> create(@RequestBody KeduBoardCommentCommentReqDTO2 keduBoardCommentCommentReqDTO2){
        boolean isSuccess = keduBoardService2.addCommentComment(keduBoardCommentCommentReqDTO2);
        return ResponseEntity.ok(isSuccess);
    }

    @PostMapping("/update")
    public ResponseEntity<Boolean> update(@RequestParam Long id, // 대댓글 id
                                          @RequestBody KeduBoardCommentCommentReqDTO2 keduBoardCommentCommentReqDTO2,
                                          @RequestParam int memberId){ // 회원 확인을 해야지
        boolean isSuccess = keduBoardService2.updateCommentComment(id, memberId,keduBoardCommentCommentReqDTO2);
        return ResponseEntity.ok(isSuccess);
    }

    @PostMapping("/delete")
    public ResponseEntity<Boolean> delete(@RequestParam Long id, @RequestParam int memberId){
        boolean isSuccess = keduBoardService2.deleteCommentComment(id,memberId );
        return ResponseEntity.ok(isSuccess);
    }
}
