//package com.capstone.project.member.repository;
//
//import com.capstone.project.member.dto.request.MemberRequestDto;
//import com.capstone.project.member.dto.response.MemberResponseDto;
//import com.capstone.project.member.entity.Member;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//@ExtendWith(SpringExtension.class)
//@SpringBootTest
//@TestPropertySource(locations = "classpath:application-test.properties")
//@Slf4j
//public class MemberRepositoryTest {
//
//    @Autowired
//    MemberRepository memberRepository;
//
//    @Test
//    @DisplayName("멤버정보확인")
//    public void 멤버_정보좀_가져와() {
//        // 1. MemberRequestDto 생성
//        MemberRequestDto memberRequestDto = new MemberRequestDto();
//        memberRequestDto.setEmail("agape@naver");
//        memberRequestDto.setName("김요한");
//        memberRequestDto.setPassword("agape99");
//        memberRequestDto.setPhoneNumber("010-9027-7477");
//
//        // 2. MemberRequestDto를 Member 엔티티로 변환
//        Member member = new Member();
//        member.setEmail(memberRequestDto.getEmail());
//        member.setName(memberRequestDto.getName());
//        member.setPassword(memberRequestDto.getPassword()); // 비밀번호는 암호화해서 저장해야 할 수 있음
//        member.setPhoneNumber(memberRequestDto.getPhoneNumber());
//
//        // 3. Member 엔티티 저장
//        memberRepository.save(member);
//
//        // 4. 저장된 Member를 조회
//        int userId = 1;
//        Member savedMember = memberRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
//
//        // 5. MemberResponseDto로 변환
//        MemberResponseDto memberResponseDto = new MemberResponseDto();
//        memberResponseDto.setMemberId(savedMember.getMemberId());
//        memberResponseDto.setName(savedMember.getName());
//        memberResponseDto.setEmail(savedMember.getEmail());
//        memberResponseDto.setPhoneNumber(savedMember.getPhoneNumber());
//
//        // 6. DTO 내용 로그로 출력
//        log.info("Member Response DTO: {}", memberResponseDto);
//    }
//
//}
