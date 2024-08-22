package com.pigeon_stargram.sns_clone.controller.login;

import com.pigeon_stargram.sns_clone.config.auth.annotation.NewUserEmail;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.login.request.RequestLoginDto;
import com.pigeon_stargram.sns_clone.dto.login.request.RequestForgotPasswordDto;
import com.pigeon_stargram.sns_clone.dto.login.request.RequestRegisterDto;
import com.pigeon_stargram.sns_clone.dto.login.request.ResetPasswordDto;
import com.pigeon_stargram.sns_clone.dto.login.response.UserEmailInfoDto;
import com.pigeon_stargram.sns_clone.dto.login.response.UserInfoDto;
import com.pigeon_stargram.sns_clone.service.login.LoginService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/session")
@RestController
public class LoginController {

    private final LoginService loginService;
    private final HttpSession httpSession;

    @GetMapping("/user-info")
    public UserEmailInfoDto getCurrentMemberEmail(@NewUserEmail String email){
        return new UserEmailInfoDto(email);
    }
    
    @PostMapping("/register")
    public void register(@RequestBody RequestRegisterDto request){

        loginService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody RequestLoginDto request) {
        User user = loginService.findLoginUser(request);

        if (user != null) {
            log.info("login success");
            httpSession.setAttribute("user", new SessionUser(user));
            return ResponseEntity.ok(new UserInfoDto(user));
        } else {
            log.info("login fail");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }
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
    public void resetPassword(@RequestBody ResetPasswordDto request) {
        loginService.resetPassword(request.getToken(), request.getNewPassword());
    }


}
