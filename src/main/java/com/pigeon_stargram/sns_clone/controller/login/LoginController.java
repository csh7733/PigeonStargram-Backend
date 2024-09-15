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

@RestController
@RequestMapping("/api/session")
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/user-info")
    public UserEmailInfoDto getCurrentMemberEmail(@NewUserEmail String email) {
        logControllerMethod("getCurrentMemberEmail", email);

        return toUserEmailInfoDto(email);
    }

    @PostMapping("/register")
    public void register(@NewUserEmail String email,
                         @RequestBody RequestRegisterDto request) {
        logControllerMethod("register", email, request);

        loginService.register(email, request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody RequestLoginDto request) {

        return loginService.login(request);
    }

    @PostMapping("/logout")
    public void logout() {

        loginService.logout();
    }

    @PostMapping("/password")
    public ResponseEntity<String> sendPasswordResetLink(@RequestBody RequestForgotPasswordDto request) {
        String email = request.getEmail();
        loginService.sendPasswordResetLink(email);

        return ResponseEntity.ok("Password reset link has been sent to your email.");
    }

    @PutMapping("/password")
    public void resetPassword(@RequestBody RequestResetPasswordDto request) {
        logControllerMethod("resetPassword", request);

        loginService.resetPassword(request);
    }


}
