package com.capstone.project.forum.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 게시글 응답 DTO
 * API 응답에 필요한 게시글 정보를 포함
 */
@Data
@Builder
@AllArgsConstructor
public class ForumPostResponseDto {

    private Integer id; // 게시글 ID
    private String title; // 게시글 제목
    private String content; // 게시글 내용
    private String authorName; // 작성자 이름
    private Integer memberId; // 작성자 ID
    private Boolean sticky; // 상단 고정 여부
    private Integer viewsCount; // 조회수
    private Integer likesCount; // 좋아요 수
    private Boolean hidden; // 숨김 여부
    private String removedBy; // 삭제자 정보
    private String editedByTitle; // 제목 수정자 정보
    private String editedByContent; // 내용 수정자 정보
    private Boolean locked; // 수정 불가능 여부 (관리자 수정 시 true)
    private LocalDateTime createdAt; // 생성 시간
    private LocalDateTime updatedAt; // 수정 시간
    private Boolean editedTitleByAdmin; // 제목이 관리자에 의해 수정되었는지 여부
    private Boolean editedContentByAdmin; // 내용이 관리자에 의해 수정되었는지 여부
    private List<String> fileUrls; // 첨부 파일 URL 목록 (단일 또는 다중 모두 가능)

    /**
     * 제목이 관리자에 의해 수정되었는지 여부를 반환
     * - editedByTitle 필드가 "ADMIN"인지 확인
     */
    public Boolean getEditedTitleByAdmin() {
        return "ADMIN".equals(this.editedByTitle);
    }

    /**
     * 내용이 관리자에 의해 수정되었는지 여부를 반환
     * - editedByContent 필드가 "ADMIN"인지 확인
     */
    public Boolean getEditedContentByAdmin() {
        return "ADMIN".equals(this.editedByContent);
    }

    /**
     * editedByTitle 값 설정
     * - 설정 시 editedTitleByAdmin 필드의 값도 자동으로 업데이트
     */
    public void setEditedByTitle(String editedByTitle) {
        this.editedByTitle = editedByTitle;
        this.editedTitleByAdmin = "ADMIN".equals(editedByTitle);
    }

    /**
     * editedByContent 값 설정
     * - 설정 시 editedContentByAdmin 필드의 값도 자동으로 업데이트
     */
    public void setEditedByContent(String editedByContent) {
        this.editedByContent = editedByContent;
        this.editedContentByAdmin = "ADMIN".equals(editedByContent);
    }
}
