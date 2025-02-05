package com.capstone.project.member.service;

import com.capstone.project.jwt.repository.RefreshTokenRepository;
import com.capstone.project.member.dto.request.MemberRequestDto;
import com.capstone.project.member.dto.response.MemberResponseDto;
import com.capstone.project.member.entity.Member;
import com.capstone.project.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 회원 서비스 클래스
 * <p>
 * 회원 관련 비즈니스 로직을 처리합니다.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 회원 프로필 조회
     *
     * @param memberId 조회할 회원 ID
     * @return MemberResponseDto 회원 프로필 정보
     * @throws EntityNotFoundException 회원을 찾을 수 없을 경우 예외 발생
     */
    public MemberResponseDto getMemberProfile(Integer memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with ID: " + memberId));

        // 프로필 사진 URL이 없으면 기본 URL 사용
        String profilePictureUrl = member.getProfilePictureUrl();
        if (profilePictureUrl == null || profilePictureUrl.isEmpty()) {
            profilePictureUrl = "https://firebasestorage.googleapis.com/v0/b/kh-react-firebase.firebasestorage.app/o/default-profile-picture-url.jpg?alt=media&token=16b39451-4ee9-4bdd-adc9-78b6cda4d4bb";
        }

        log.info("Fetching profile for memberId={} | Name={} | Email={} | ProfilePicture={}",
                memberId, member.getName(), member.getEmail(), profilePictureUrl);

        return new MemberResponseDto(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getPhoneNumber(),
                member.getCurrentCompany(),
                member.getShowCompany(),
                profilePictureUrl,
                member.getRole().name()
        );
    }

    /**
     * 회원 프로필 수정
     *
     * @param memberId   수정할 회원 ID
     * @param requestDto 수정할 정보가 담긴 DTO
     * @return 수정된 회원 프로필 정보 DTO
     */
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

    /**
     * 회원 탈퇴 (계정 삭제)
     *
     * @param memberId 삭제할 회원 ID
     * @throws EntityNotFoundException 회원을 찾을 수 없을 경우 예외 발생
     */
    public void deleteMember(Integer memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with ID: " + memberId));

        // 먼저 연관된 RefreshToken 삭제 (Integer -> Long으로 변환)
        refreshTokenRepository.deleteByMemberId(memberId);

        memberRepository.delete(member);
    }

    /**
     * 현재 로그인된 회원 정보 조회
     *
     * @param userId 로그인된 회원 ID
     * @return MemberResponseDto 현재 회원 정보
     * @throws RuntimeException 회원을 찾을 수 없을 경우 예외 발생
     */
    public MemberResponseDto getCurrentUser(Integer userId) {
        log.error("유저 아이디 값 : {}", userId);
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        return new MemberResponseDto(member);
    }

    /**
     * 이메일로 회원 정보 수정
     *
     * @param email      수정할 회원 이메일
     * @param requestDto 수정할 정보가 담긴 DTO
     * @return 수정된 회원 정보 DTO
     * @throws RuntimeException 회원을 찾을 수 없을 경우 예외 발생
     */
    @Transactional
    public MemberResponseDto updateUser(String email, MemberRequestDto requestDto) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        member.setName(requestDto.getName());
        member.setPhoneNumber(requestDto.getPhoneNumber());
        member.setCurrentCompany(requestDto.getCurrentCompany());
        member.setShowCompany(requestDto.getShowCompany());

        return new MemberResponseDto(member);
    }

    /**
     * 사용자 역할 업데이트
     *
     * @param email 회원 이메일
     * @param role  업데이트할 역할 (대소문자 무관)
     * @throws RuntimeException 회원을 찾을 수 없을 경우 예외 발생
     */
    @Transactional
    public void updateUserRole(String email, String role) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        member.setRole(Member.Role.valueOf(role.toUpperCase()));
    }

    /**
     * 이메일로 계정 삭제
     *
     * @param email 삭제할 회원 이메일
     * @throws RuntimeException 회원을 찾을 수 없을 경우 예외 발생
     */
    @Transactional
    public void deleteAccount(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        memberRepository.delete(member);
    }

    /**
     * 해당 회원이 ADMIN 권한을 가지고 있는지 확인
     *
     * @param memberId 확인할 회원 ID
     * @return true: ADMIN, false: USER
     * @throws IllegalArgumentException 잘못된 회원 ID일 경우 예외 발생
     */
    public boolean isAdmin(Integer memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID: " + memberId));
        return member.getRole() == Member.Role.ADMIN;
    }

    /**
     * 특정 회원의 친구 ID 목록을 반환
     *
     * @param memberId 현재 로그인된 회원 ID
     * @return 친구들의 회원 ID 리스트
     * @throws EntityNotFoundException 회원을 찾을 수 없을 경우 예외 발생
     */
    @Transactional(readOnly = true)
    public List<Integer> getFriendIds(Integer memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with ID: " + memberId));

        return member.getFriends().stream()
                .map(Member::getId)
                .collect(Collectors.toList());
    }

    /**
     * 랜덤 친구 추천 목록 조회
     * <p>
     * 현재 로그인한 회원을 제외하고 최근 가입한 회원 중 5명을 랜덤 추천합니다.
     * </p>
     *
     * @param memberId 현재 로그인한 회원 ID
     * @return 추천된 회원 정보 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<MemberResponseDto> getSuggestedFriends(Integer memberId) {
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "registeredAt")); // 최신 회원 5명 추천

        List<Member> suggestedMembers = memberRepository.findRandomMembersExcludingUser(memberId, pageable);

        return suggestedMembers.stream()
                .map(member -> new MemberResponseDto(
                        member.getId(),
                        member.getEmail(),
                        member.getName(),
                        member.getPhoneNumber(),
                        member.getCurrentCompany(),
                        member.getShowCompany(),
                        member.getProfilePictureUrl(),
                        member.getRole().name()
                ))
                .collect(Collectors.toList());
    }
}
