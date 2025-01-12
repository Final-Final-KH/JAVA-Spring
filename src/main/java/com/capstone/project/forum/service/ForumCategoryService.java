package com.capstone.project.forum.service;

import com.capstone.project.forum.dto.response.ForumCategoryDto;
import com.capstone.project.forum.entity.ForumCategory;
import com.capstone.project.forum.entity.ForumPost;
import com.capstone.project.forum.repository.ForumCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ForumCategoryService
 * 포럼 카테고리 관련 비즈니스 로직 처리
 */
@Service
@RequiredArgsConstructor
@Slf4j // 로그 기록을 위한 어노테이션 추가
public class ForumCategoryService {
    private final ForumCategoryRepository categoryRepository; // 카테고리 데이터 접근을 위한 Repository

    /**
     * 모든 카테고리를 최신 수정 시간 순으로 가져오기
     * @return ForumCategoryDto 리스트 (최신 수정 시간 순)
     */
    public List<ForumCategoryDto> getAllCategories() {
        log.info("Fetching all forum categories ordered by update time"); // 로그 기록

        // 최신 수정 시간 기준으로 정렬된 카테고리 가져오기
        return categoryRepository.findAllCategoriesOrderedByUpdateTime()
                .stream() // Stream으로 변환
                .map(category -> new ForumCategoryDto( // ForumCategory -> ForumCategoryDto 매핑
                        category.getId(), // 카테고리 ID
                        category.getTitle(), // 카테고리 제목
                        category.getDescription(), // 카테고리 설명
                        (int) categoryRepository.countPostsByCategoryId(category.getId()), // 해당 카테고리의 게시글 수 (long -> int 캐스팅)
                        category.getPosts().stream()
                                .mapToInt(post -> post.getComments().size()) // 각 게시글의 댓글 수를 합산
                                .sum(), // 댓글 총합 계산
                        category.getUpdatedAt() // 카테고리 마지막 수정 시간
                ))
                .collect(Collectors.toList()); // 리스트로 변환
    }

    /**
     * 특정 카테고리에 대한 최신 게시글 정보 가져오기
     * @param categoryId 조회할 카테고리 ID
     * @return ForumCategoryDto (Optional)
     */
    public Optional<ForumCategoryDto> getCategoryWithLatestPost(Integer categoryId) {
        log.info("Fetching latest post for category ID: {}", categoryId); // 로그 기록

        // 카테고리 조회 후 최신 게시글 정보 매핑
        return categoryRepository.findById(categoryId).map(category -> {
            // 최신 게시글 가져오기 (최신 순으로 정렬된 첫 번째 게시글)
            ForumPost latestPost = categoryRepository.findLatestPostsByCategoryId(categoryId)
                    .stream()
                    .findFirst()
                    .orElse(null); // 게시글이 없을 경우 null

            // ForumCategoryDto 생성 및 반환
            return new ForumCategoryDto(
                    category.getId(), // 카테고리 ID
                    category.getTitle(), // 카테고리 제목
                    category.getDescription(), // 카테고리 설명
                    (int) categoryRepository.countPostsByCategoryId(categoryId), // 게시글 수 (long -> int 캐스팅)
                    latestPost != null ? latestPost.getComments().size() : 0, // 최신 게시글의 댓글 수
                    category.getUpdatedAt() // 카테고리 마지막 수정 시간
            );
        });
    }
}
