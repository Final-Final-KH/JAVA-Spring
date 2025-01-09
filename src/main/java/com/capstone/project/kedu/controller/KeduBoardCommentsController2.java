package com.capstone.project.kedu.controller;

import com.capstone.project.kedu.dto.board.KeduBoardCommentReqDTO2;
import com.capstone.project.kedu.dto.board.KeduBoardCommentResDTO2;
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
@RequestMapping("/board_comment")
@RequiredArgsConstructor
public class KeduBoardCommentsController2 {
    private final KeduBoardService2 keduBoardService2;

    @GetMapping("/list")
    public Map<String ,Object> list (@RequestParam(value = "id") Long id){
        Map<String, Object> resultMap = new HashMap<>();
        List<KeduBoardCommentResDTO2> list = keduBoardService2.findComment(id);
        resultMap.put("list", list);
        return resultMap;
    }

    @PostMapping("/{boardId}/comments")
    public ResponseEntity<Boolean> addComment(@PathVariable Long boardId, @RequestBody KeduBoardCommentReqDTO2 keduBoardCommentReqDTO2){
        boolean isSuccess = keduBoardService2.addComment(boardId, keduBoardCommentReqDTO2);
        return ResponseEntity.ok(isSuccess);
    }

    @PostMapping("/{boardId}/update/{commentId}")
    public ResponseEntity<Boolean> updateComment(@PathVariable Long boardId, @RequestBody KeduBoardCommentReqDTO2 keduBoardCommentReqDTO2,
                                                 @PathVariable Long commentId){
        boolean isSuccess = keduBoardService2.updateComment(boardId, keduBoardCommentReqDTO2, commentId);
        return ResponseEntity.ok(isSuccess);
    }

    @PostMapping("/{boardId}/delete/{commentId}")
    public ResponseEntity<Boolean> deleteComment(@PathVariable Long boardId, @RequestParam(value = "member_id") int memberId,
                                                 @PathVariable Long commentId){
        boolean isSuccess = keduBoardService2.deleteComment(boardId, memberId, commentId);
        return ResponseEntity.ok(isSuccess);
    }


}
