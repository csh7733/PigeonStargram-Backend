package com.pigeon_stargram.sns_clone.controller.login;

import com.pigeon_stargram.sns_clone.config.auth.annotation.NewUserEmail;
import com.pigeon_stargram.sns_clone.dto.login.request.RequestForgotPasswordDto;
import com.pigeon_stargram.sns_clone.dto.login.request.RequestLoginDto;
import com.pigeon_stargram.sns_clone.dto.login.request.RequestRegisterDto;
import com.pigeon_stargram.sns_clone.dto.login.request.RequestResetPasswordDto;
import com.pigeon_stargram.sns_clone.dto.login.response.UserEmailInfoDto;
import com.pigeon_stargram.sns_clone.service.login.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.pigeon_stargram.sns_clone.dto.user.UserDtoConverter.toUserEmailInfoDto;
import static com.pigeon_stargram.sns_clone.util.LogUtil.logControllerMethod;

/**
 * 로그인 관련 기능을 처리하는 컨트롤러입니다.
 * 로그인, 로그아웃, 비밀번호 재설정, 회원가입과 같은 기능을 제공합니다.
 */
@RestController
@RequestMapping("/api/session")
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final LoginService loginService;

    /**
     * 현재 로그인한 사용자의 이메일 정보를 반환합니다.
     *
     * @param email 로그인한 사용자의 이메일
     * @return 사용자의 이메일 정보를 담은 DTO 객체
     */
    @GetMapping("/user-info")
    public UserEmailInfoDto getCurrentMemberEmail(@NewUserEmail String email) {
        logControllerMethod("getCurrentMemberEmail", email);

        return toUserEmailInfoDto(email);
    }

    /**
     * 새로운 사용자를 등록합니다.
     *
     * @param email 회원가입하려는 사용자의 이메일
     * @param request 회원가입 요청에 대한 DTO 객체
     */
    @PostMapping("/register")
    public void register(@NewUserEmail String email,
                         @RequestBody RequestRegisterDto request) {
        logControllerMethod("register", email, request);

        loginService.register(email, request);
    }

    /**
     * 사용자 로그인을 처리합니다.
     *
     * @param request 로그인 요청에 대한 DTO 객체
     * @return 로그인 성공 또는 실패에 대한 응답
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody RequestLoginDto request) {
        logControllerMethod("login", request);

        return loginService.login(request);
    }

    /**
     * 사용자 로그아웃을 처리합니다.
     */
    @PostMapping("/logout")
    public void logout() {
        logControllerMethod("logout");

        loginService.logout();
    }

    /**
     * 비밀번호 재설정 링크를 사용자의 이메일로 전송합니다.
     *
     * @param request 비밀번호 재설정 요청에 대한 DTO 객체
     * @return 비밀번호 재설정 링크 전송 성공 응답
     */
    @PostMapping("/password")
    public ResponseEntity<String> sendPasswordResetLink(@RequestBody RequestForgotPasswordDto request) {
        logControllerMethod("sendPasswordResetLink", request);

        loginService.sendPasswordResetLink(request.getEmail());

        return ResponseEntity.ok("Password reset link has been sent to your email.");
    }

    /**
     * 사용자의 비밀번호를 재설정합니다.
     *
     * @param request 비밀번호 재설정 요청에 대한 DTO 객체
     */
    @PutMapping("/password")
    public void resetPassword(@RequestBody RequestResetPasswordDto request) {
        logControllerMethod("resetPassword", request);

        loginService.resetPassword(request);
    }


}
