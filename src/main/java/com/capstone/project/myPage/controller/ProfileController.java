package com.capstone.project.myPage.controller;


import com.capstone.project.myPage.dto.MemberProfileResponseDto;
import com.capstone.project.myPage.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ProfileController {

    private final ProfileService profileService;

    // 마이페이지 데이터 조회
    @GetMapping
    public ResponseEntity<MemberProfileResponseDto> getMyPage(Authentication authentication) {
        Integer memberId = Integer.valueOf(authentication.getName()); // 인증 정보로 회원 ID 추출
        MemberProfileResponseDto responseDto = profileService.getMyPageData(memberId); // 서비스 호출하여 데이터 가져오기
        return ResponseEntity.ok(responseDto); // 응답 반환
    }

    // 이름 업데이트
    @PutMapping("/name")
    public ResponseEntity<String> updateName (
            Authentication authentication, @RequestParam String name) {
        int memberId = Integer.parseInt(authentication.getName());
        profileService.updateName(name, memberId);
        return ResponseEntity.ok("Name updated successfully");
    }

    //전화번호 업데이트
    @PutMapping("/phone")
    public ResponseEntity<String> phoneNumberUpdate (
            Authentication authentication, @RequestParam String phone) {
        int memberId = Integer.parseInt(authentication.getName());
        profileService.updatePhone(phone, memberId);
        return ResponseEntity.ok("Phone number updated successfully");
    }


    // 지역 업데이트
    @PutMapping("/location")
    public ResponseEntity<String> locationUpdate (
            Authentication authentication, @RequestParam String location) {
        int memberId = Integer.parseInt(authentication.getName());
        profileService.updateLocation(location, memberId);
        return ResponseEntity.ok("Location updated successfully");
    }


    // 자기소개 업데이트
    @PutMapping("/bio")
    public ResponseEntity<String> updateBio(
            Authentication authentication,
            @RequestParam String bio) {
        int memberId = Integer.parseInt(authentication.getName());
        profileService.updateBio(bio, memberId);
        return ResponseEntity.ok("Bio updated successfully");
    }

    // 이력서 URL 업데이트
    @PutMapping("/resume")
    public ResponseEntity<String> updateResumeUrl(
            Authentication authentication,
            @RequestParam String resume) {
        int memberId = Integer.parseInt(authentication.getName());
        profileService.updateResumeUrl(resume, memberId);
        return ResponseEntity.ok("Resume URL updated successfully");
    }

    // 전문분야 업데이트
    @PutMapping("/skills")
    public ResponseEntity<String> updateSkills(
            Authentication authentication,
            @RequestParam String skills) {
        int memberId = Integer.parseInt(authentication.getName());
        profileService.updateSkills(skills, memberId);
        return ResponseEntity.ok("Skills updated successfully");
    }

}