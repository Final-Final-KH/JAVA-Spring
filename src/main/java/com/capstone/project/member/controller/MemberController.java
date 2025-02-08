package com.capstone.project.member.controller;

import com.capstone.project.member.dto.request.MemberRequestDto;
import com.capstone.project.member.dto.response.MemberResponseDto;
import com.capstone.project.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 회원 Controller
 * <p>
 * 회원 관련 API 엔드포인트를 제공합니다.
 * </p>
 */
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원 프로필 조회 API
     *
     * @param memberId 조회할 회원 ID
     * @return 회원 프로필 정보
     */
    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponseDto> getMemberProfile(@PathVariable Integer memberId) {
        MemberResponseDto profile = memberService.getMemberProfile(memberId);
        return ResponseEntity.ok(profile);
    }

    /**
     * 회원 프로필 수정 API
     *
     * @param memberId   수정할 회원 ID
     * @param requestDto 수정할 정보 DTO
     * @return 수정된 회원 프로필 정보
     */
    @PutMapping("/{memberId}")
    public ResponseEntity<MemberResponseDto> updateMemberProfile(
            @PathVariable Integer memberId,
            @RequestBody MemberRequestDto requestDto) {
        MemberResponseDto updatedProfile = memberService.updateMemberProfile(memberId, requestDto);
        return ResponseEntity.ok(updatedProfile);
    }

    /**
     * 회원 탈퇴 API
     *
     * @param memberId 삭제할 회원 ID
     * @return HTTP 상태 코드 204 (No Content)
     */
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable Integer memberId) {
        memberService.deleteMember(memberId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 랜덤 친구 추천 API
     *
     * @param memberId 현재 로그인한 회원 ID (요청 파라미터)
     * @return 추천된 회원 정보 리스트
     */
    @GetMapping("/suggested")
    public List<MemberResponseDto> getSuggestedFriends(@RequestParam Integer memberId) {
        return memberService.getSuggestedFriends(memberId);
    }
}
