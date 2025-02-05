//package com.capstone.project.signupTest;
//
//
//import com.capstone.project.member.controller.AuthController;
//import com.capstone.project.member.dto.TokenDto;
//import com.capstone.project.member.dto.request.LoginRequestDto;
//import com.capstone.project.member.dto.request.MemberRequestDto;
//import com.capstone.project.member.dto.response.MemberResponseDto;
//import com.capstone.project.member.repository.MemberRepository;
//import com.capstone.project.member.service.AuthService;
//import com.capstone.project.member.service.MemberService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.http.MediaType;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//@AutoConfigureMockMvc(addFilters = true)
//class AuthControllerTest {
//
//    @InjectMocks
//    private AuthController authController;
//
//    @Mock
//    private AuthService authService;
//
//    @Mock
//    private MemberService memberService;
//
//    @Mock
//    private MemberRepository memberRepository;
//
//    private MockMvc mockMvc;
//
//    private ObjectMapper objectMapper;
//
//
//
//    @BeforeEach
//    public void init() {
//        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
//        objectMapper = new ObjectMapper();
//    }
//
//
//
//    @DisplayName("회원가입 테스트")
//    @Test
//    void testSignUp() throws Exception {
//
//        MemberRequestDto requestDto = new MemberRequestDto();
//        requestDto.setEmail("test@example.com");
//        requestDto.setPassword("password");
//        requestDto.setName("Test Member");
//
//        MemberResponseDto responseDto = new MemberResponseDto();
//        responseDto.setMemberId(1);
//        responseDto.setEmail("test@example.com");
//        responseDto.setName("Test Member");
//
//        when(authService.signUp(any(MemberRequestDto.class))).thenReturn(responseDto);
//
//        mockMvc.perform(post("/auth/signup")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(objectMapper.writeValueAsString(requestDto)))
//                .andDo(print())
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.memberId").value(1))
//                .andExpect(jsonPath("$.email").value("test@example.com"))
//                .andExpect(jsonPath("$.name").value("Test Member"));
//    }
//    @Test
//    void testLogin() throws Exception {
//        // Arrange
//        LoginRequestDto loginRequest = new LoginRequestDto();
//        loginRequest.setEmail("test@example.com");
//        loginRequest.setPassword("password");
//
//        TokenDto tokenDto = new TokenDto();
//        tokenDto.setGrantType("Bearer");
//        tokenDto.setAccessToken("access-token");
//        tokenDto.setRefreshToken("refresh-token");
//
//        // Reactive 타입에 맞게 Mock 설정
//        when(authService.login(any(LoginRequestDto.class))).thenReturn(tokenDto);
//
//        // Act & Assert
//        mockMvc.perform(post("/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(loginRequest)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.grantType").value("Bearer"))
//                .andExpect(jsonPath("$.accessToken").value("access-token"))
//                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
//    }
//    @Test
//    void testRefreshToken() throws Exception {
//
//        String refreshToken = "refresh-token";
//        String newAccessToken = "new-access-token";
//
//
//        when(authService.createAccessToken(Mockito.anyString())).thenReturn(newAccessToken);
//
//
//        mockMvc.perform(post("/auth/refresh")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("\"" + refreshToken + "\""))  // 정확한 문자열 형식으로 전송
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$").value("new-access-token"));
//    }
//
//    @DisplayName("현재 사용자 정보 조회 테스트")
//    @Test
//    void testGetCurrentUser() throws Exception {
//        // Authentication 객체를 생성
//        Authentication authentication = new UsernamePasswordAuthenticationToken("1", "password", new ArrayList<>());
//
//        // MemberResponseDto 객체 설정
//        MemberResponseDto responseDto = new MemberResponseDto();
//        responseDto.setMemberId(1);
//        responseDto.setEmail("test@example.com");
//        responseDto.setName("Test Member");
//
//        // memberService의 getCurrentUser 메서드가 호출되었을 때 responseDto를 반환하도록 설정
//        when(memberService.getCurrentUser(1)).thenReturn(responseDto);
//
//        // 테스트 실행
//        mockMvc.perform(get("/auth/current-user")
//                        .with(authentication(authentication))) // 요청에 Authentication 객체 주입
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.memberId").value(1))
//                .andExpect(jsonPath("$.email").value("test@example.com"))
//                .andExpect(jsonPath("$.name").value("Test Member"));
//    }
//
//    @Test
//    void testUpdateUser() throws Exception {
//        // Arrange
//        String username = "1";  // 인증된 사용자 ID
//        MemberRequestDto requestDto = new MemberRequestDto();
//        requestDto.setEmail("updated@example.com");
//        requestDto.setPassword("new-password");
//        requestDto.setName("Updated Member");
//
//        MemberResponseDto updatedMember = new MemberResponseDto();
//        updatedMember.setMemberId(1);
//        updatedMember.setEmail("updated@example.com");
//        updatedMember.setName("Updated Member");
//
//        when(memberService.updateUser(any(String.class), any(MemberRequestDto.class)))
//                .thenReturn(updatedMember);
//
//        // Act & Assert
//        mockMvc.perform(put("/auth/current-user")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(requestDto))
//                        .header("Authorization", "Bearer valid-access-token"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.memberId").value(1))
//                .andExpect(jsonPath("$.email").value("updated@example.com"))
//                .andExpect(jsonPath("$.name").value("Updated Member"));
//    }
//
//    @Test
//    void testUpdateRole() throws Exception {
//        // Arrange
//        String email = "test@example.com";
//        String role = "ADMIN";
//
//        // Act & Assert
//        mockMvc.perform(put("/auth/role")
//                        .param("email", email)
//                        .param("role", role))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }
//    @Test
//    void testDeleteAccount() throws Exception {
//        // Arrange
//        String username = "1";  // 인증된 사용자 ID
//
//        // Act & Assert
//        mockMvc.perform(delete("/auth/current-user")
//                        .header("Authorization", "Bearer valid-access-token"))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }
//    @Test
//    void testCheckEmail() throws Exception {
//        // Arrange
//        String email = "test@example.com";
//        boolean isAvailable = true;
//
//        when(memberRepository.existsByEmail(email)).thenReturn(false);  // 이메일이 사용 가능하면 false를 반환
//
//        Map<String, String> request = new HashMap<>();
//        request.put("email", email);
//
//        // Act & Assert
//        mockMvc.perform(post("/auth/check-email")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.isAvailable").value(true));  // 이메일이 사용 가능하므로 true
//    }
//
//
//
//}
