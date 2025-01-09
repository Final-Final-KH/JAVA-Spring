package com.capstone.project.kedu.service;

import com.capstone.project.kedu.dto.board.KeduBoardDetailResDTO2;
import com.capstone.project.kedu.dto.board.KeduBoardRegReqDTO2;
import com.capstone.project.kedu.dto.board.KeduBoardReqDTO2;
import com.capstone.project.kedu.dto.board.KeduBoardResDTO2;
import com.capstone.project.kedu.entity.board.KeduBoardEntity2;
import com.capstone.project.kedu.entity.edu.AcademyEntity2;
import com.capstone.project.kedu.entity.edu.CourseEntity2;
import com.capstone.project.kedu.repository.board.KeduBoardRepository2;
import com.capstone.project.kedu.repository.edu.AcademyRepository2;
import com.capstone.project.kedu.repository.edu.CourseRepository2;
import com.capstone.project.member.entity.Member;
import com.capstone.project.member.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeduBoardService2 {
    private final KeduBoardRepository2 keduBoardRepository2;
    private final MemberRepository memberRepository;
    private final CourseRepository2 courseRepository2;
    private final AcademyRepository2 academyRepository2;

    public List<KeduBoardResDTO2> board(KeduBoardReqDTO2 keduBoardReqDTO2) {
        List<KeduBoardEntity2> keduBoardEntity2 = keduBoardRepository2.findByAcademyIdAndCourseId(keduBoardReqDTO2.getAcademy_id(),keduBoardReqDTO2.getCourse_id());
        List<KeduBoardResDTO2> keduBoardResDTO2s = new ArrayList<>();
        for (KeduBoardEntity2 keduBoardEntity : keduBoardEntity2){
            keduBoardResDTO2s.add(convertEntityToDto(keduBoardEntity));
        }
        return keduBoardResDTO2s;
    }

    public KeduBoardDetailResDTO2 boardDetail(Long id) {
       KeduBoardEntity2 keduBoardEntity2 = keduBoardRepository2.findById(id)
               .orElseThrow(()-> new RuntimeException("해당 게시물이 존재 하지 않습니다."));
       return convertEntityToDtoDetail(keduBoardEntity2);
    }

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
