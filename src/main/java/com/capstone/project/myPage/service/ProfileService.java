package com.capstone.project.myPage.service;


import com.capstone.project.member.entity.Member;
import com.capstone.project.member.repository.MemberRepository;
import com.capstone.project.myPage.dto.*;
import com.capstone.project.myPage.entity.Profile;
import com.capstone.project.myPage.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {
    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private MemberRepository memberRepository;


    // 마이페이지 데이터 조회
    public MemberProfileResponseDto getMyPageData(Integer memberId) {
        MemberProfileResponseDto dto = profileRepository.findMemberProfileById(memberId);

        // 전화번호에 하이픈 추가
        if (dto.getPhoneNumber() != null) {
            dto.setPhoneNumber(formatPhoneNumber(dto.getPhoneNumber()));
        }

        return dto;
    }

    // 하이픈 추가 유틸리티 메서드
    private String formatPhoneNumber(String phoneNumber) {
        // 예시: 01012345678 -> 010-1234-5678
        if (phoneNumber.length() == 11) {
            return phoneNumber.replaceAll("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3");
        } else if (phoneNumber.length() == 10) {
            return phoneNumber.replaceAll("(\\d{3})(\\d{3})(\\d{4})", "$1-$2-$3");
        }
        return phoneNumber; // 기본적으로 원본 반환
    }
    // 이름 수정
    public void updateName(String name, int memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new RuntimeException("맴버를 조회 할 수 없습니다."));

        member.setName(name);
        memberRepository.save(member);
    }
    // 전화번호 수정
    public void updatePhone(String phone, int memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new RuntimeException("맴버를 조회 할 수 없습니다."));

        // 전화번호에서 하이픈 제거 후 저장
        member.setPhoneNumber(phone.replaceAll("-", ""));
        memberRepository.save(member);
    }

    // 지역 수정
    public void updateLocation(String location, int memberId) {
        log.error("멤버 아이디 :{}",memberId);
        Profile profile = profileRepository.findByMemberId(memberId)
                .orElseGet(() -> createProfileForMember(memberId));

        profile.setLocation(location);
        profileRepository.save(profile);
    }


    // 자기소개 수정
    public void updateBio(String bio, int memberId) {
        Profile profile = profileRepository.findByMemberId(memberId)
                .orElseGet(() -> createProfileForMember(memberId));

        profile.setBio(bio);
        profileRepository.save(profile);
    }

    // 이력서 URL 수정
    public void updateResumeUrl(String resumeUrl, int memberId) {
        Profile profile = profileRepository.findByMemberId(memberId)
                .orElseGet(() -> createProfileForMember(memberId));

        profile.setResumeUrl(resumeUrl);
        profileRepository.save(profile);
    }
    public void updateSkills (String skills, int memberId) {
        Profile profile = profileRepository.findByMemberId(memberId)
                .orElseGet(() -> createProfileForMember(memberId));

        profile.setSkills(skills);
        profileRepository.save(profile);
    }

    // 프로필이 없으면 생성하는 메서드
    private Profile createProfileForMember(Integer memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        Profile profile = new Profile();

        profile.setMember(member); // Member와 연결
        return profileRepository.save(profile);
    }


}