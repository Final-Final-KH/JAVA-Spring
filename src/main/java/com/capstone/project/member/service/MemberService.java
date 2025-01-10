package com.capstone.project.member.service;

import com.capstone.project.member.dto.request.MemberRequestDto;
import com.capstone.project.member.dto.response.MemberResponseDto;
import com.capstone.project.member.entity.Member;
import com.capstone.project.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;

    // Fetch member profile
    public MemberResponseDto getMemberProfile(Integer memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with ID: " + memberId));
        String profilePictureUrl = member.getProfilePictureUrl();
        if (profilePictureUrl == null || profilePictureUrl.isEmpty()) {
            profilePictureUrl = "https://firebasestorage.googleapis.com/v0/b/kh-react-firebase.firebasestorage.app/o/default-profile-picture-url.jpg?alt=media&token=16b39451-4ee9-4bdd-adc9-78b6cda4d4bb";
            // 디폴트 사진 값 -- 내가 올린 파이어 베이스에서 실험적 적용
        }
        return new MemberResponseDto(member.getId(), member.getEmail(), member.getName(),
                member.getPhoneNumber(), member.getCurrentCompany(), member.getShowCompany(), profilePictureUrl);
    }

    // Update member profile
    public MemberResponseDto updateMemberProfile(Integer memberId, MemberRequestDto requestDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with ID: " + memberId));

        member.setName(requestDto.getName());
        member.setPhoneNumber(requestDto.getPhoneNumber());
        member.setCurrentCompany(requestDto.getCurrentCompany());
        member.setShowCompany(requestDto.getShowCompany());

        Member updatedMember = memberRepository.save(member);
        return new MemberResponseDto(updatedMember);
    }

    // Delete member account
    public void deleteMember(Integer memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with ID: " + memberId));
        memberRepository.delete(member);
    }
}
