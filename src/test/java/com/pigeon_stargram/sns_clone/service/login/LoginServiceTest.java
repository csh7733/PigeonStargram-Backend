package com.pigeon_stargram.sns_clone.service.login;

import com.pigeon_stargram.sns_clone.domain.login.PasswordResetToken;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.login.request.RequestLoginDto;
import com.pigeon_stargram.sns_clone.dto.login.request.RequestRegisterDto;
import com.pigeon_stargram.sns_clone.exception.login.EmailNotSentException;
import com.pigeon_stargram.sns_clone.exception.login.TokenExpiredException;
import com.pigeon_stargram.sns_clone.exception.login.TokenNotFoundException;
import com.pigeon_stargram.sns_clone.exception.user.MultipleUsersFoundException;
import com.pigeon_stargram.sns_clone.exception.login.RegisterFailException;
import com.pigeon_stargram.sns_clone.exception.user.UserNotFoundException;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import com.pigeon_stargram.sns_clone.test_util.MailExceptionImpl;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @InjectMocks
    private LoginService loginService;

    @Mock
    private UserService userService;

    @Mock
    private HttpSession httpSession;

    @Mock
    private PasswordResetTokenService tokenService;

    @Mock
    private JavaMailSender mailSender;

    private User user;
    private PasswordResetToken resetToken;

    private RequestLoginDto loginDto;
    private RequestRegisterDto registerDto;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(1L)
                .name("John Doe")
                .company("TechCorp")
                .workEmail("john.doe@techcorp.com")
                .personalEmail("john.doe@gmail.com")
                .workPhone("+123456789")
                .personalPhone("+987654321")
                .location("New York, USA")
                .avatar("http://example.com/avatar.jpg")
                .birthdayText("1985-12-15")
                .onlineStatus("Online")
                .password("1q2w3e4r")
                .build();

        loginDto = RequestLoginDto.builder()
                .email("dto-email")
                .password("dto-password")
                .build();

        registerDto = RequestRegisterDto.builder()
                .email("dto-email")
                .password("dto-password")
                .username("dto-username")
                .company("dto-company")
                .avatar("dto-avatar")
                .personalPhone("dto-personalPhone")
                .build();

        resetToken = PasswordResetToken.builder()
                .token("dto-token")
                .expiryDate(LocalDateTime.now())
                .email("dto-email")
                .build();


    }

    @Test
    @DisplayName("로그인 양식으로 유저 찾기 - 성공")
    public void testFindLoginUser() {
        //given
        when(userService.findByWorkEmailAndPassword(anyString(), anyString()))
                .thenReturn(user);

        //when
        User findUser = loginService.findLoginUser(loginDto);

        //then
        assertThat(user).isEqualTo(findUser);
    }

    @Test
    @DisplayName("로그인 양식으로 유저 찾기 - 유저 없음")
    public void testFindLoginUserUserNotFound() {
        //given
        when(userService.findByWorkEmailAndPassword(anyString(), anyString()))
                .thenThrow(UserNotFoundException.class);

        //when

        //then
        assertThatThrownBy(() -> {
            loginService.findLoginUser(loginDto);
        }).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("로그인 양식으로 유저 찾기 - 여러 유저")
    public void testFindLoginUserMultipleUsersFound() {
        //given
        when(userService.findByWorkEmailAndPassword(anyString(), anyString()))
                .thenThrow(MultipleUsersFoundException.class);

        //when

        //then
        assertThatThrownBy(() -> {
            loginService.findLoginUser(loginDto);
        }).isInstanceOf(MultipleUsersFoundException.class);
    }

    @Test
    @DisplayName("로그아웃")
    public void testLogout() {
        //given

        //when
        loginService.logout();

        //then
        verify(httpSession, times(1))
                .invalidate();
    }

    @Test
    @DisplayName("RequestRegisterDto로 회원 가입 - 성공")
    public void testRegisterSuccess() {
        //given
        when(userService.save(any(RequestRegisterDto.class)))
                .thenReturn(user);

        //when
        loginService.register(registerDto);

        //then
        verify(userService, times(1))
                .save(registerDto);
    }

    @Test
    @DisplayName("RequestRegisterDto로 회원 가입 - 중복된 이메일")
    public void testRegisterEmailDuplicated() {
        //given
        when(userService.save(any(RequestRegisterDto.class)))
                .thenThrow(RegisterFailException.class);

        //when

        //then
        assertThatThrownBy(() -> {
            loginService.register(registerDto);
        }).isInstanceOf(RegisterFailException.class);
    }

    @Test
    @DisplayName("비밀번호 변경 이메일 전송 - 성공")
    public void testSendPasswordResetLinkSuccess() {
        //given
        when(userService.findByWorkEmail(anyString())).thenReturn(user);
        when(tokenService.createToken(anyString())).thenReturn(resetToken);

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        //when
        loginService.sendPasswordResetLink("test-email");

        //then
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    @DisplayName("비밀번호 변경 이메일 전송 - 이메일 없음")
    public void testSendPasswordResetLinkUserNotFound() {
        //given
        when(userService.findByWorkEmail(anyString()))
                .thenThrow(UserNotFoundException.class);

        //when

        //then
        assertThatThrownBy(() -> {
            loginService.sendPasswordResetLink("test-email");
        }).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("비밀번호 변경 이메일 전송 - 전송 실패")
    public void testSendPasswordResetLinkEmailNotSent() {
        //given
        when(userService.findByWorkEmail(anyString())).thenReturn(user);
        when(tokenService.createToken(anyString())).thenReturn(resetToken);

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        doThrow(MailExceptionImpl.class)
                .when(mailSender).send(any(MimeMessage.class));
        //when

        //then
        assertThatThrownBy(() -> {
            loginService.sendPasswordResetLink("test-email");
        }).isInstanceOf(EmailNotSentException.class);
    }

    @Test
    @DisplayName("비밀번호 변경 - 성공")
    public void testResetPasswordSuccess() {
        //given
        when(tokenService.validateToken(anyString())).thenReturn(resetToken);
        when(tokenService.extractEmail(anyString())).thenReturn("test-email");
        when(userService.findByWorkEmail(anyString())).thenReturn(user);
        when(userService.updatePassword(anyLong(), anyString())).thenReturn(user);

        //when
        User updateUser = loginService.resetPassword("test-token", "new-password");

        //then
        assertThat(user).isEqualTo(updateUser);
    }

    @Test
    @DisplayName("비밀번호 변경 - 존재하지 않는 토큰")
    public void testResetPasswordTokenNotFound() {
        //given
        when(tokenService.validateToken(anyString()))
                .thenThrow(TokenNotFoundException.class);

        //when

        //then
        assertThatThrownBy(() -> {
            loginService.resetPassword("test-token", "new-password");
        }).isInstanceOf(TokenNotFoundException.class);
    }

    @Test
    @DisplayName("비밀번호 변경 - 만료된 토큰")
    public void testResetPasswordTokenExpired() {
        //given
        when(tokenService.validateToken(anyString())).thenThrow(TokenExpiredException.class);

        //when

        //then
        assertThatThrownBy(() -> {
            loginService.resetPassword("test-token", "new-password");
        }).isInstanceOf(TokenExpiredException.class);
    }

    @Test
    @DisplayName("비밀번호 변경 - 토큰의 이메일 유저를 찾지 못함")
    public void testResetPasswordUserNotFound() {
        //given
        when(tokenService.validateToken(anyString())).thenReturn(resetToken);
        when(tokenService.extractEmail(anyString())).thenReturn("test-email");
        when(userService.findByWorkEmail(anyString()))
                .thenThrow(UserNotFoundException.class);

        //when

        //then
        assertThatThrownBy(() -> {
            loginService.resetPassword("test-token", "new-password");
        }).isInstanceOf(UserNotFoundException.class);
    }

}