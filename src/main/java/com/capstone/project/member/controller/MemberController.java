package com.capstone.project.member.controller;

import com.capstone.project.member.dto.request.MemberRequestDto;
import com.capstone.project.member.dto.response.MemberResponseDto;
import com.capstone.project.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    // Get member profile
    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponseDto> getMemberProfile(@PathVariable Integer memberId) {
        MemberResponseDto profile = memberService.getMemberProfile(memberId);
        return ResponseEntity.ok(profile);
    }

    // Update member profile
    @PutMapping("/{memberId}")
    public ResponseEntity<MemberResponseDto> updateMemberProfile(
            @PathVariable Integer memberId,
            @RequestBody MemberRequestDto requestDto) {
        MemberResponseDto updatedProfile = memberService.updateMemberProfile(memberId, requestDto);
        return ResponseEntity.ok(updatedProfile);
    }

    // Delete account
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable Integer memberId) {
        memberService.deleteMember(memberId);
        return ResponseEntity.noContent().build();
    }
}