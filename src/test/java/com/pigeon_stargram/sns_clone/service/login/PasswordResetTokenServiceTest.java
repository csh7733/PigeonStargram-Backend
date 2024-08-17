package com.pigeon_stargram.sns_clone.service.login;

import com.pigeon_stargram.sns_clone.domain.login.PasswordResetToken;
import com.pigeon_stargram.sns_clone.exception.login.TokenExpiredException;
import com.pigeon_stargram.sns_clone.exception.login.TokenNotFoundException;
import com.pigeon_stargram.sns_clone.repository.login.PasswordResetTokenRepository;
import com.pigeon_stargram.sns_clone.util.PasswordResetTokenGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetTokenServiceTest {

    @InjectMocks
    PasswordResetTokenService tokenService;

    @Mock
    PasswordResetTokenRepository tokenRepository;

    @Mock
    PasswordResetTokenGenerator tokenGenerator;

    PasswordResetToken validToken;
    PasswordResetToken invalidToken;

    @BeforeEach
    public void setUp() {
        validToken = PasswordResetToken.builder()
                .token("test-token")
                .email("test-email")
                .expiryDate(LocalDateTime.of(2400, 1, 1, 0, 0))
                .build();

        invalidToken = PasswordResetToken.builder()
                .token("test-token")
                .email("test-email")
                .expiryDate(LocalDateTime.of(2000, 1, 1, 0, 0))
                .build();
    }

    @Test
    @DisplayName("비밀번호 변경 토큰 생성 - 성공")
    public void testCreateTokenSuccess() {
        //given
        when(tokenGenerator.generateToken())
                .thenReturn("test-token");
        when(tokenGenerator.calculateExpiryDate())
                .thenReturn(LocalDateTime.now());
        when(tokenRepository.save(any(PasswordResetToken.class)))
                .thenReturn(validToken);

        //when
        PasswordResetToken createToken = tokenService.createToken("test-email");

        //then
        assertThat(validToken).isEqualTo(createToken);
    }

    @Test
    @DisplayName("토큰 검증 - 성공")
    public void testValidateTokenSuccess() {
        //given
        when(tokenRepository.findByToken(anyString()))
                .thenReturn(Optional.of(validToken));

        //when
        PasswordResetToken validateToken = tokenService.validateToken("test-token");

        //then
        assertThat(validToken).isEqualTo(validateToken);
    }

    @Test
    @DisplayName("토큰 검증 - 존재하지 않는 토큰")
    public void testValidateTokenTokenNotFound() {
        //given
        when(tokenRepository.findByToken(anyString()))
                .thenReturn(Optional.empty());

        //when

        //then
        assertThatThrownBy(() -> {
            tokenService.validateToken("test-token");
        }).isInstanceOf(TokenNotFoundException.class);
    }

    @Test
    @DisplayName("토큰 검증 - 만료된 토큰")
    public void testValidateTokenTokenExpired() {
        //given
        when(tokenRepository.findByToken(anyString()))
                .thenReturn(Optional.of(invalidToken));

        //when

        //then
        assertThatThrownBy(() -> {
            tokenService.validateToken("test-token");
        }).isInstanceOf(TokenExpiredException.class);
    }

    @Test
    @DisplayName("토큰에서 이메일 추출 - 성공")
    public void testExtractEmailSuccess() {
        //given
        when(tokenRepository.findByToken(anyString()))
                .thenReturn(Optional.of(validToken));

        //when
        String extractEmail = tokenService.extractEmail("test-token");

        //then
        assertThat(validToken.getEmail()).isEqualTo(extractEmail);
    }

    @Test
    @DisplayName("토큰에서 이메일 추출 - 존재하지 않는 토큰")
    public void testExtractEmailTokenNotFound() {
        //given
        when(tokenRepository.findByToken(anyString()))
                .thenReturn(Optional.empty());

        //when

        //then
        assertThatThrownBy(() -> {
            tokenService.extractEmail("test-token");
        }).isInstanceOf(TokenNotFoundException.class);
    }
    

}