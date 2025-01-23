package com.capstone.project.kedu.service;

import com.capstone.project.kedu.dto.comment.AcademyCommentReqDTO2;
import com.capstone.project.kedu.dto.comment.AcademyCommentResDTO2;
import com.capstone.project.kedu.entity.comment.AcademyCommentEntity2;
import com.capstone.project.kedu.entity.edu.AcademyEntity2;
import com.capstone.project.kedu.repository.comment.AcademyCommentRepository2;
import com.capstone.project.kedu.repository.edu.AcademyRepository2;
import com.capstone.project.member.entity.Member;
import com.capstone.project.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AcademyCommentService2 {
    private final AcademyCommentRepository2 academyCommentRepository2;
    private final MemberRepository memberRepository;
    private final AcademyRepository2 academyRepository2;

    public AcademyCommentService2(AcademyCommentRepository2 academyCommentRepository2, MemberRepository memberRepository, AcademyRepository2 academyRepository2) {
        this.academyCommentRepository2 = academyCommentRepository2;
        this.memberRepository = memberRepository;
        this.academyRepository2 = academyRepository2;
    }

    // 각 컬럼별 평균 + 총 평균 반환
    public List<AcademyCommentResDTO2> sub_total_avg(Long academyId) {
        AcademyEntity2 academyEntity2 = academyRepository2.findById(academyId)
                .orElseThrow(()-> new RuntimeException("해당학원은 존재하지 않습니다."));
        // 평균을 계산하는 쿼리 호출
        List<Object[]> results = academyCommentRepository2.findAverageScoresForAcademy(academyId);

        // 결과 리스트를 저장할 리스트
        List<AcademyCommentResDTO2> dtoList = new ArrayList<>();

        for (Object[] averages : results) {
            // DTO 생성
            AcademyCommentResDTO2 dto = new AcademyCommentResDTO2();

            // 각 컬럼별 평균값 설정
            dto.setAvgJob((averages[0] != null) ? (double) averages[0] : 0);
            dto.setAvgLecture((averages[1] != null) ? (double) averages[1] : 0);
            dto.setAvgFacilities((averages[2] != null) ? (double) averages[2] : 0);
            dto.setAvgTeacher((averages[3] != null) ? (double) averages[3] : 0);
            dto.setAvgBooks((averages[4] != null) ? (double) averages[4] : 0);
            dto.setAvgService((averages[5] != null) ? (double) averages[5] : 0);
            dto.setAcademy_id(academyEntity2.getAcademyId());
            // 전체 평균 계산
            double totalAvg = (dto.getAvgJob() + dto.getAvgLecture() + dto.getAvgFacilities() +
                    dto.getAvgTeacher() + dto.getAvgBooks() + dto.getAvgService()) / 6.0;

            // 전체 평균 설정
            dto.setTotalAvg(totalAvg);

            // DTO를 리스트에 추가
            dtoList.add(dto);
        }

        return dtoList; // 리스트 반환
    }
    // 취업률
    public Optional<Integer> empl(Long academyId) {
        Optional<Integer> empl = academyCommentRepository2.findEmploymentOutcomePercentage(academyId);
        return  empl;
    }
    // 학원에 대한 간략한 코멘트 생성
    public boolean create(AcademyCommentReqDTO2 academyCommentReqDTO2) {
        try{
            Member member = memberRepository.findById(academyCommentReqDTO2.getMember_id())
                    .orElseThrow(()-> new RuntimeException("해당 회원이 존재 하지 않습니다."));
            AcademyEntity2 academyEntity2 = academyRepository2.findById(academyCommentReqDTO2.getAcademy_id())
                    .orElseThrow(()-> new RuntimeException("해당 학원이 존재하지 않습니다."));
            AcademyCommentEntity2 academyCommentEntity2 = new AcademyCommentEntity2();
            academyCommentEntity2.setEmployee_outcome(academyCommentReqDTO2.isEmployee_outcome());
            academyCommentEntity2.setAcademyEntity2(academyEntity2);
            academyCommentEntity2.setMember(member);
            academyCommentEntity2.setJob(academyCommentReqDTO2.getJob());
            academyCommentEntity2.setService(academyCommentReqDTO2.getService());
            academyCommentEntity2.setBooks(academyCommentReqDTO2.getBooks());
            academyCommentEntity2.setFacilities(academyCommentReqDTO2.getFacilities());
            academyCommentEntity2.setTeacher(academyCommentReqDTO2.getTeacher());
            academyCommentEntity2.setLecture(academyCommentReqDTO2.getLecture());
            academyCommentRepository2.save(academyCommentEntity2);
            return true;
        }catch (Exception e ){
            log.error("학원 코멘트 등록 실패 : {}", e.getMessage());
            return false;
        }
    }
    // 나의 학원 코멘트
    public List<AcademyCommentResDTO2> myAcademyComment(int memberId) {
        List<AcademyCommentEntity2> academyCommentEntity2s = academyCommentRepository2.findByMemberId(memberId);
        List<AcademyCommentResDTO2> academyCommentResDTO2s = new ArrayList<>();
        for(AcademyCommentEntity2 academyCommentEntity2 : academyCommentEntity2s){
            academyCommentResDTO2s.add(convertEntityToDto(academyCommentEntity2));
        }
        return academyCommentResDTO2s;
    }
    // 학원 코멘트 리스트
    public List<AcademyCommentResDTO2> list(Long id) {
        List<AcademyCommentEntity2> academyCommentEntity2 = academyCommentRepository2.findByAcademyEntity2AcademyId(id);
        List<AcademyCommentResDTO2> academyCommentResDTO2s = new ArrayList<>();
        for(AcademyCommentEntity2 academyCommentEntity : academyCommentEntity2){
            academyCommentResDTO2s.add(convertEntityToDto(academyCommentEntity));
        }
        return academyCommentResDTO2s;
    }
    @Transactional
    public boolean update(AcademyCommentReqDTO2 academyCommentReqDTO2, Long id) {
        try {
            // 기존 평가 엔티티 가져오기
            AcademyCommentEntity2 academyCommentEntity2 = academyCommentRepository2.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("해당 학원 평가가 존재하지 않습니다."));

            // 회원 엔티티 가져오기
            Member member = memberRepository.findById(academyCommentReqDTO2.getMember_id())
                    .orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다."));

            // 작성자 확인: 회원이 해당 평가를 작성한 사람인지 확인
            if (!academyCommentEntity2.getMember().getMemberId().equals(academyCommentReqDTO2.getMember_id())) {
                throw new RuntimeException("회원은 자신이 작성한 평가만 수정할 수 있습니다.");
            }

            // 기존 엔티티의 값 수정
            academyCommentEntity2.setLecture(academyCommentReqDTO2.getLecture());
            academyCommentEntity2.setService(academyCommentReqDTO2.getService());
            academyCommentEntity2.setJob(academyCommentReqDTO2.getJob());
            academyCommentEntity2.setBooks(academyCommentReqDTO2.getBooks());
            academyCommentEntity2.setTeacher(academyCommentReqDTO2.getTeacher());
            academyCommentEntity2.setFacilities(academyCommentReqDTO2.getFacilities());
            academyCommentEntity2.setEmployee_outcome(academyCommentReqDTO2.isEmployee_outcome());
            academyCommentEntity2.setMember(member);

            // 수정된 엔티티 저장
            academyCommentRepository2.save(academyCommentEntity2);

            return true;
        } catch (Exception e) {
            log.error("학원 평가 정보 수정 중 오류 발생 : {}", e.getMessage());
            return false;
        }
    }


    public boolean delete(Long commentId, int memberId) {
        try{
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(()-> new RuntimeException("해당 회원이 존재 하지 않습니다"));
            AcademyCommentEntity2 academyCommentEntity2 = academyCommentRepository2.findById(commentId)
                    .orElseThrow(()-> new RuntimeException("해당 게시물이 존재하지 않습니다."));

            if(!academyCommentEntity2.getMember().getMemberId().equals(memberId)){
                throw new RuntimeException("해당 게시물은 해당 회원의 게시물이 아닙니다.");
            }
            academyCommentRepository2.deleteById(commentId);
            return true;
        }catch (Exception e) {
            log.error("게시물 삭제에 실패했습니다. : {}", e.getMessage());
            return false;
        }
    }
    private AcademyCommentResDTO2 convertEntityToDto(AcademyCommentEntity2 academyCommentEntity) {
        AcademyCommentResDTO2 academyCommentResDTO2 = new AcademyCommentResDTO2();
        academyCommentResDTO2.setAcademy_comment_id(academyCommentEntity.getAcademy_comment_id());
        academyCommentResDTO2.setAcademy_id(academyCommentEntity.getAcademyEntity2().getAcademyId());
        academyCommentResDTO2.setAvgService(academyCommentEntity.getService());
        academyCommentResDTO2.setAvgJob(academyCommentEntity.getJob());
        academyCommentResDTO2.setAvgBooks(academyCommentEntity.getBooks());
        academyCommentResDTO2.setAvgFacilities(academyCommentEntity.getFacilities());
        academyCommentResDTO2.setAvgLecture(academyCommentEntity.getLecture());
        academyCommentResDTO2.setAvgTeacher(academyCommentEntity.getTeacher());
        academyCommentResDTO2.setMember_id(academyCommentEntity.getMember().getMemberId());
        return academyCommentResDTO2;
    }
}
