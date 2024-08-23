package com.pigeon_stargram.sns_clone.controller.login;

import com.pigeon_stargram.sns_clone.config.auth.annotation.NewUserEmail;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.login.request.RequestLoginDto;
import com.pigeon_stargram.sns_clone.dto.login.request.RequestForgotPasswordDto;
import com.pigeon_stargram.sns_clone.dto.login.request.RequestRegisterDto;
import com.pigeon_stargram.sns_clone.dto.login.request.RequestResetPasswordDto;
import com.pigeon_stargram.sns_clone.dto.login.response.UserEmailInfoDto;
import com.pigeon_stargram.sns_clone.dto.login.response.UserInfoDto;
import com.pigeon_stargram.sns_clone.service.login.LoginBuilder;
import com.pigeon_stargram.sns_clone.service.login.LoginService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.pigeon_stargram.sns_clone.service.login.LoginBuilder.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/session")
@RestController
public class LoginController {

    private final LoginService loginService;
    private final HttpSession httpSession;

    @GetMapping("/user-info")
    public UserEmailInfoDto getCurrentMemberEmail(@NewUserEmail String email){

        return buildUserEmailInfoDto(email);
    }
    
    @PostMapping("/register")
    public void register(@RequestBody RequestRegisterDto request){

        loginService.register(request);
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

        loginService.resetPassword(request);
    }


}
