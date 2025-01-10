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

@Service
@RequiredArgsConstructor
@Slf4j // 로그 기록을 위한 어노테이션 추가
public class ForumCategoryService {
    private final ForumCategoryRepository categoryRepository;

    // 모든 포럼 카테고리를 최신 순으로 가져오기
    public List<ForumCategoryDto> getAllCategories() {
        log.info("Fetching all forum categories ordered by update time"); // 카테고리 조회 시작 로그
        return categoryRepository.findAllCategoriesOrderedByUpdateTime()
                .stream()
                .map(category -> new ForumCategoryDto(
                        category.getId(), // 카테고리 ID
                        category.getTitle(), // 카테고리 제목
                        category.getDescription(), // 카테고리 설명
                        category.getPosts().size(), // 게시글 수
                        category.getPosts().stream()
                                .mapToInt(post -> post.getComments().size())
                                .sum(), // 댓글 수
                        category.getUpdatedAt() // 마지막 수정 시간
                ))
                .collect(Collectors.toList());
    }

    // 특정 카테고리에 대한 최신 게시글 정보 가져오기
    public Optional<ForumCategoryDto> getCategoryWithLatestPost(Integer categoryId) {
        log.info("Fetching latest post for category ID: {}", categoryId);
        return categoryRepository.findById(categoryId).map(category -> {
            var latestPost = category.getPosts().stream()
                    .max(Comparator.comparing(ForumPost::getCreatedAt))
                    .orElse(null);

            return new ForumCategoryDto(
                    category.getId(),
                    category.getTitle(),
                    category.getDescription(),
                    category.getPosts().size(),
                    category.getPosts().stream().mapToInt(post -> post.getComments().size()).sum(),
                    category.getUpdatedAt()
            );
        });
    }
}
