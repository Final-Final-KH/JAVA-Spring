//package com.capstone.project.forum.controller;
//
//import com.capstone.project.forum.dto.response.ForumPostResponseDto;
//import com.capstone.project.forum.dto.response.PaginationDto;
//import com.capstone.project.forum.service.ForumPostService;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//
//@WebMvcTest(ForumPostController.class)
//public class ForumPostControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private ForumPostService postService;
//
//    @Test
//    public void testGetPostsByCategory() throws Exception {
//        // 페이지네이션과 게시글 데이터를 포함한 응답 객체를 생성
//        PaginationDto<ForumPostResponseDto> paginationDto = new PaginationDto<>(List.of(
//                ForumPostResponseDto.builder()
//                        .id(1)
//                        .title("Title 1")
//                        .content("Content 1")
//                        .authorName("User1")
//                        .memberId(1)
//                        .sticky(false)
//                        .viewsCount(0)
//                        .likesCount(0)
//                        .hidden(false)
//                        .removedBy(null)
//                        .createdAt(LocalDateTime.now())
//                        .updatedAt(LocalDateTime.now())
//                        .fileUrls(List.of("http://example.com/file1"))
//                        .build()
//        ), 0, 1, 1L);
//
//        // Mockito를 사용하여 서비스 계층의 동작을 모의함
//        Mockito.when(postService.getPostsByCategory(1, 0, 10)).thenReturn(paginationDto);
//
//        // MockMvc를 사용하여 GET 요청을 테스트
//        mockMvc.perform(get("/api/forums/posts")
//                        .param("categoryId", "1") // 요청 매개변수로 카테고리 ID 제공
//                        .param("page", "1") // 요청 매개변수로 페이지 번호 제공
//                        .param("size", "10")) // 요청 매개변수로 페이지 크기 제공
//                .andExpect(status().isOk()) // HTTP 200 상태 코드 예상
//                .andExpect(jsonPath("$.data[0].title").value("Title 1")); // 응답 데이터의 제목 검증
//    }
//}
