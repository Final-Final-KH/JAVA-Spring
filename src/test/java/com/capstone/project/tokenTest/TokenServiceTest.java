package com.capstone.project.tokenTest;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.capstone.project.jwt.TokenProvider;
import com.capstone.project.jwt.entity.RefreshToken;
import com.capstone.project.jwt.repository.RefreshTokenRepository;
import com.capstone.project.member.dto.TokenDto;
import com.capstone.project.member.entity.Member;
import com.capstone.project.member.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.token.TokenService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

public class TokenServiceTest {

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private AuthService authService;

    private String validRefreshToken;
    private String invalidRefreshToken;
    private Member member;
    private TokenDto newTokenDto;
    private RefreshToken storedToken;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        validRefreshToken = "valid-refresh-token";
        invalidRefreshToken = "invalid-refresh-token";

        member = new Member();  // 적절한 Member 객체 생성
        member.setId(1);

        newTokenDto = TokenDto.builder()
                .grantType("Bearer")
                .accessToken("new-access-token")
                .refreshToken("new-refresh-token")
                .accessTokenExpiresIn(System.currentTimeMillis() + 1000 * 60 * 60 * 24L)
                .refreshTokenExpiresIn(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7L)
                .build();

        storedToken = new RefreshToken();
        storedToken.setMember(member);
        storedToken.setRefreshToken(validRefreshToken);
        storedToken.setExpirationDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")).plusDays(7));
    }

    @Test
    void testReissueAccessToken_validRefreshToken() {
        // Arrange
        when(refreshTokenRepository.findByRefreshToken(validRefreshToken)).thenReturn(Optional.of(storedToken));
        when(tokenProvider.validateToken(validRefreshToken)).thenReturn(true);
        when(tokenProvider.getAuthentication(validRefreshToken)).thenReturn(mock(Authentication.class));
        when(tokenProvider.generateTokenDto(any())).thenReturn(newTokenDto);

        // Act
        TokenDto result = authService.reissueAccessToken(validRefreshToken);

        // Assert
        assertNotNull(result);
        assertEquals(newTokenDto.getAccessToken(), result.getAccessToken());
        assertEquals(newTokenDto.getRefreshToken(), result.getRefreshToken());
    }

    @Test
    void testReissueAccessToken_invalidRefreshToken() {
        // Arrange
        when(tokenProvider.validateToken(invalidRefreshToken)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () ->authService.reissueAccessToken(invalidRefreshToken));
        assertEquals("유효하지 않은 리프레시 토큰", exception.getMessage());
    }

    @Test
    void testReissueAccessToken_refreshTokenNotFoundInDB() {
        // Arrange
        when(tokenProvider.validateToken(validRefreshToken)).thenReturn(true);
        when(refreshTokenRepository.findByRefreshToken(validRefreshToken)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.reissueAccessToken(validRefreshToken));
        assertEquals("리프레시 토큰이 존재하지 않습니다.", exception.getMessage());
    }
}
