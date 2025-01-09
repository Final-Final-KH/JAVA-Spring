package com.capstone.project.kedu.service;

import com.capstone.project.kedu.dto.board.*;
import com.capstone.project.kedu.entity.board.KeduBoardCommentEntity2;
import com.capstone.project.kedu.entity.board.KeduBoardCommentsCommentsEntity2;
import com.capstone.project.kedu.entity.board.KeduBoardEntity2;
import com.capstone.project.kedu.entity.edu.AcademyEntity2;
import com.capstone.project.kedu.entity.edu.CourseEntity2;
import com.capstone.project.kedu.repository.board.KeduBoardCommentCommentRepository2;
import com.capstone.project.kedu.repository.board.KeduBoardCommentRepository2;
import com.capstone.project.kedu.repository.board.KeduBoardRepository2;
import com.capstone.project.kedu.repository.edu.AcademyRepository2;
import com.capstone.project.kedu.repository.edu.CourseRepository2;
import com.capstone.project.member.entity.Member;
import com.capstone.project.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeduBoardService2 {
    private final KeduBoardRepository2 keduBoardRepository2;
    private final MemberRepository memberRepository;
    private final CourseRepository2 courseRepository2;
    private final AcademyRepository2 academyRepository2;
    private final KeduBoardCommentRepository2 keduBoardCommentRepository2;
    private final KeduBoardCommentCommentRepository2 keduBoardCommentCommentRepository2;
    // 생성자에서 모든 의존성 주입
    @Autowired
    public KeduBoardService2(MemberRepository memberRepository,
                             KeduBoardRepository2 keduBoardRepository2,
                             CourseRepository2 courseRepository2,
                             AcademyRepository2 academyRepository2,
                             KeduBoardCommentRepository2 keduBoardCommentRepository2,
                             KeduBoardCommentCommentRepository2 keduBoardCommentCommentRepository2) {
        this.memberRepository = memberRepository;
        this.keduBoardRepository2 = keduBoardRepository2;
        this.courseRepository2 = courseRepository2;
        this.academyRepository2 = academyRepository2;
        this.keduBoardCommentRepository2 = keduBoardCommentRepository2;
        this.keduBoardCommentCommentRepository2 = keduBoardCommentCommentRepository2;
    }

    // 전체 게시판 조회
    public List<KeduBoardResDTO2> board(KeduBoardReqDTO2 keduBoardReqDTO2) {
        List<KeduBoardEntity2> keduBoardEntity2 = keduBoardRepository2.findByAcademyIdAndCourseId(keduBoardReqDTO2.getAcademy_id(),keduBoardReqDTO2.getCourse_id());
        List<KeduBoardResDTO2> keduBoardResDTO2s = new ArrayList<>();
        for (KeduBoardEntity2 keduBoardEntity : keduBoardEntity2){
            keduBoardResDTO2s.add(convertEntityToDto(keduBoardEntity));
        }
        return keduBoardResDTO2s;
    }

    // 게시글 상세 조회
    public KeduBoardDetailResDTO2 boardDetail(Long id) {
       KeduBoardEntity2 keduBoardEntity2 = keduBoardRepository2.findById(id)
               .orElseThrow(()-> new RuntimeException("해당 게시물이 존재 하지 않습니다."));
       return convertEntityToDtoDetail(keduBoardEntity2);
    }

    // 게시글 업데이트
    public boolean updateBoard(KeduBoardUpdateReqDTO2 keduBoardUpdateReqDTO2) {
        try{
            KeduBoardEntity2 keduBoardEntity2 = keduBoardRepository2.findById(keduBoardUpdateReqDTO2.getId())
                    .orElseThrow(()-> new RuntimeException("해당 게시물이 존재 하지 않습니다."));
            KeduBoardEntity2 keduBoardEntity = new KeduBoardEntity2();
            keduBoardEntity2.setId(keduBoardEntity2.getId());
            keduBoardEntity2.setTitle(keduBoardUpdateReqDTO2.getTitle());
            keduBoardEntity2.setContent(keduBoardUpdateReqDTO2.getContent());
            keduBoardEntity2.setRegDate(keduBoardUpdateReqDTO2.getRegDate());
            keduBoardRepository2.save(keduBoardEntity2);
            return true;
        }catch (Exception e) {
            log.error("게시글 정보 수정 : {}", e.getMessage());
            return false;
        }
    }

    // 게시판 삭제
    public boolean deleteBoard(int memberId, Long id) {
        try {
            // 해당 회원이 존재하는지 확인
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new RuntimeException("해당 회원이 존재하지 않습니다."));

            // 해당 게시물이 존재하는지 확인
            KeduBoardEntity2 keduBoardEntity2 = keduBoardRepository2.findById(id)
                    .orElseThrow(() -> new RuntimeException("해당 게시물이 존재하지 않습니다."));

            // 게시물의 작성자가 요청한 회원과 동일한지 확인
            if (!keduBoardEntity2.getMember().getId().equals(memberId)) {
                throw new RuntimeException("해당 게시물은 해당 회원의 게시물이 아닙니다.");
            }

            // 해당 게시물 삭제
            keduBoardRepository2.deleteById(id);
            return true;

        } catch (Exception e) {
            log.error("게시물 삭제에 실패했습니다. : {}", e.getMessage());
            return false;
        }
    }

    // 댓글 삭제
    public boolean deleteComment(Long boardId, int memberId, Long commentId) {
        try{
            KeduBoardEntity2 keduBoardEntity2 = keduBoardRepository2.findById(boardId)
                    .orElseThrow(()-> new RuntimeException("게시글이 존재 하지 않습니다."));
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(()-> new RuntimeException("회원 정보가 존재하지 않습니다."));
            KeduBoardCommentEntity2 targetComment = null;

            for(KeduBoardCommentEntity2 keduBoardCommentEntity3 : keduBoardEntity2.getComments()){
                if(keduBoardCommentEntity3.getBoard_comment_id().equals(commentId)){
                    targetComment = keduBoardCommentEntity3;
                    break;
                }
            }
            if(targetComment == null) {
                log.error("해당 댓글이 존재하지 않습니다.");
            }
            keduBoardEntity2.removeComment(targetComment);
            keduBoardRepository2.save(keduBoardEntity2);
            return true;
        }catch (Exception e) {
            log.error("댓글 제거 실패 : {}",e.getMessage());
            return false;
        }
    }

    // 게시글 등록
    public boolean newBoard(KeduBoardRegReqDTO2 keduBoardRegReqDTO2) {
        try{
            Member member = memberRepository.findById(keduBoardRegReqDTO2.getMember_id())
                    .orElseThrow(()-> new RuntimeException("해당 회원이 존재하지 않습니다."));
            CourseEntity2 courseEntity2 = courseRepository2.findById(keduBoardRegReqDTO2.getCourse_id())
                    .orElseThrow(()-> new RuntimeException("해당 강좌가 존재하지 않습니다."));
            AcademyEntity2 academyEntity2 = academyRepository2.findById(keduBoardRegReqDTO2.getAcademy_id())
                    .orElseThrow(()-> new RuntimeException("해당 학원이 존재하지 않습니다."));
            KeduBoardEntity2 keduBoardEntity2 = new KeduBoardEntity2();
            keduBoardEntity2.setTitle(keduBoardRegReqDTO2.getTitle());
            keduBoardEntity2.setContent(keduBoardRegReqDTO2.getContent());
            keduBoardEntity2.setAcademyEntity2(academyEntity2);
            keduBoardEntity2.setCourseEntity2(courseEntity2);
            keduBoardEntity2.setMember(member);
            keduBoardEntity2.setUser_id(keduBoardRegReqDTO2.getUser_id());
            keduBoardRepository2.save(keduBoardEntity2);
            return true;
        }catch (Exception e ){
            log.error("게시글 등록 실패 : {}", e.getMessage());
            return false;
        }
    }

    // 댓글 수정 댓글 생성이랑 같음
    public boolean updateComment(Long boardId, KeduBoardCommentReqDTO2 keduBoardCommentReqDTO2, Long commentId) {
        try{
            KeduBoardEntity2 keduBoardEntity2 = keduBoardRepository2.findById(boardId)
                    .orElseThrow(()-> new RuntimeException("게시글이 존재 하지 않습니다."));
            Member member = memberRepository.findById(keduBoardCommentReqDTO2.getMember_id())
                    .orElseThrow(()-> new RuntimeException("회원 정보가 존재하지 않습니다."));
            KeduBoardCommentEntity2 keduBoardCommentEntity2 = keduBoardCommentRepository2.findById(commentId)
                    .orElseThrow(()-> new RuntimeException("해당 댓글이 존재 하지 않습니다."));

            KeduBoardCommentEntity2 keduBoardCommentEntity3 = new KeduBoardCommentEntity2();
            keduBoardCommentEntity3.setBoard_comment_id(keduBoardCommentEntity2.getBoard_comment_id());
            keduBoardCommentEntity3.setContent(keduBoardCommentReqDTO2.getContent());
            keduBoardCommentEntity3.setMember(member);
            keduBoardCommentEntity3.setKedu_board(keduBoardEntity2);
            keduBoardEntity2.addComment(keduBoardCommentEntity3);
            keduBoardRepository2.save(keduBoardEntity2);
            return true;
        }catch (Exception e) {
            log.error("댓글 추가 실패 : {}",e.getMessage());
            return false;
        }
    }

    // 대댓글 추가
    public boolean addCommentComment(KeduBoardCommentCommentReqDTO2 keduBoardCommentCommentReqDTO2) {
        try {
            // 게시글 확인
            KeduBoardEntity2 keduBoardEntity2 = keduBoardRepository2.findById(keduBoardCommentCommentReqDTO2.getBoard_id())
                    .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

            // 회원 확인
            Member member = memberRepository.findById(keduBoardCommentCommentReqDTO2.getMember_id())
                    .orElseThrow(() -> new RuntimeException("회원 정보가 존재하지 않습니다."));

            // 댓글 확인
            KeduBoardCommentEntity2 keduBoardCommentEntity2 = keduBoardCommentRepository2.findById(keduBoardCommentCommentReqDTO2.getComment_id())
                    .orElseThrow(() -> new RuntimeException("댓글이 존재하지 않습니다."));

            // 대댓글 엔티티 생성
            KeduBoardCommentsCommentsEntity2 keduBoardCommentsCommentsEntity2 = new KeduBoardCommentsCommentsEntity2();

            // 필드 설정
            keduBoardCommentsCommentsEntity2.setKedu_board_comment(keduBoardCommentEntity2);
            keduBoardCommentsCommentsEntity2.setMember(member);
            keduBoardCommentsCommentsEntity2.setBoard(keduBoardEntity2);
            keduBoardCommentsCommentsEntity2.setContent(keduBoardCommentCommentReqDTO2.getContent()); // 제대로 된 content 값을 설정
            keduBoardCommentsCommentsEntity2.setRegDate(LocalDateTime.now()); // 현재 시간을 등록

            // 대댓글을 댓글에 추가
            keduBoardCommentEntity2.addComment(keduBoardCommentsCommentsEntity2);

            // 댓글 업데이트 (대댓글 포함)
            keduBoardCommentRepository2.save(keduBoardCommentEntity2);

            return true;
        } catch (Exception e) {
            log.error("대댓글 추가 실패 : {}", e.getMessage());
            return false;
        }
    }

    // 댓글 추가
    public boolean addComment(Long boardId, KeduBoardCommentReqDTO2 keduBoardCommentReqDTO2) {
        try{
            KeduBoardEntity2 keduBoardEntity2 = keduBoardRepository2.findById(boardId)
                    .orElseThrow(()-> new RuntimeException("게시글이 존재 하지 않습니다."));
            Member member = memberRepository.findById(keduBoardCommentReqDTO2.getMember_id())
                    .orElseThrow(()-> new RuntimeException("회원 정보가 존재하지 않습니다."));
            KeduBoardCommentEntity2 keduBoardCommentEntity2 = new KeduBoardCommentEntity2();
            keduBoardCommentEntity2.setContent(keduBoardCommentReqDTO2.getContent());
            keduBoardCommentEntity2.setMember(member);
            keduBoardCommentEntity2.setKedu_board(keduBoardEntity2);
            keduBoardEntity2.addComment(keduBoardCommentEntity2);
            keduBoardRepository2.save(keduBoardEntity2);
            return true;
        }catch (Exception e) {
            log.error("댓글 추가 실패 : {}",e.getMessage());
            return false;
        }
    }

    // 대댓글 출력
    public List<KeduBoardCommentCommentResDTO2> findCommentComment(Long id) {
        try{
            KeduBoardCommentEntity2 keduBoardCommentEntity2 = keduBoardCommentRepository2.findById(id)
                    .orElseThrow(()-> new RuntimeException("해당 댓글은 존재하지 않습니다."));
            List<KeduBoardCommentCommentResDTO2> commentCommentResDTO2List = new ArrayList<>();

            for (KeduBoardCommentsCommentsEntity2 keduBoardCommentsCommentsEntity2 : keduBoardCommentEntity2.getComments()){
                KeduBoardCommentCommentResDTO2 keduBoardCommentCommentResDTO2 = new KeduBoardCommentCommentResDTO2();

                keduBoardCommentCommentResDTO2.setBoard_id(keduBoardCommentsCommentsEntity2.getBoard().getId());
                keduBoardCommentCommentResDTO2.setBoard_comment_id(keduBoardCommentsCommentsEntity2.getKedu_board_comment().getBoard_comment_id());
                keduBoardCommentCommentResDTO2.setBoard_comment_comment_id(keduBoardCommentsCommentsEntity2.getCommentId());
                keduBoardCommentCommentResDTO2.setContent(keduBoardCommentsCommentsEntity2.getContent());
                keduBoardCommentCommentResDTO2.setRegDate(keduBoardCommentsCommentsEntity2.getRegDate());

                commentCommentResDTO2List.add(keduBoardCommentCommentResDTO2);
            }
            return commentCommentResDTO2List;
        } catch (Exception e) {
            log.error("댓글에 대한 대댓글 조회 실패 : {}", e.getMessage());
            return null;
        }
    }

    // 댓글 리스트 출력
    public List<KeduBoardCommentResDTO2> findComment(Long id) {
        try {
            // 게시글 조회
            KeduBoardEntity2 keduBoardEntity2 = keduBoardRepository2.findById(id)
                    .orElseThrow(() -> new RuntimeException("해당 게시글이 없습니다."));

            // 댓글 리스트 초기화
            List<KeduBoardCommentResDTO2> commentResDTO2List = new ArrayList<>();

            // 댓글 리스트 조회 및 DTO로 변환
            for (KeduBoardCommentEntity2 keduBoardCommentEntity2 : keduBoardEntity2.getComments()) {
                KeduBoardCommentResDTO2 keduBoardCommentResDTO2 = new KeduBoardCommentResDTO2();

                // 댓글 정보 설정
                keduBoardCommentResDTO2.setBoard_comment_id(keduBoardCommentEntity2.getBoard_comment_id());
                keduBoardCommentResDTO2.setBoard_id(keduBoardCommentEntity2.getKedu_board().getId());
                keduBoardCommentResDTO2.setMember_id(keduBoardCommentEntity2.getMember().getMemberId());
                keduBoardCommentResDTO2.setContent(keduBoardCommentEntity2.getContent());  // 댓글 내용
                keduBoardCommentResDTO2.setRegDate(keduBoardCommentEntity2.getRegDate());  // 댓글 등록일

                // 리스트에 추가
                commentResDTO2List.add(keduBoardCommentResDTO2);
            }

            // 댓글 리스트 반환
            return commentResDTO2List;
        } catch (Exception e) {
            log.error("게시글에 대한 댓글 조회 실패 : {}", e.getMessage());
            return null;
        }
    }



    private KeduBoardResDTO2 convertEntityToDto(KeduBoardEntity2 keduBoardEntity) {
        KeduBoardResDTO2 keduBoardResDTO2 = new KeduBoardResDTO2();

        keduBoardResDTO2.setTitle(keduBoardEntity.getTitle());
        keduBoardResDTO2.setRegDate(keduBoardEntity.getRegDate());
        return keduBoardResDTO2;
    }

    private KeduBoardDetailResDTO2 convertEntityToDtoDetail(KeduBoardEntity2 keduBoardEntity2){
        KeduBoardDetailResDTO2 keduBoardDetailResDTO2 = new KeduBoardDetailResDTO2();
        keduBoardDetailResDTO2.setTitle(keduBoardEntity2.getTitle());
        keduBoardDetailResDTO2.setContent(keduBoardEntity2.getContent());
        keduBoardDetailResDTO2.setUser_id(keduBoardEntity2.getUser_id());
        keduBoardDetailResDTO2.setRegDate(keduBoardEntity2.getRegDate());
        return keduBoardDetailResDTO2;
    }



}
