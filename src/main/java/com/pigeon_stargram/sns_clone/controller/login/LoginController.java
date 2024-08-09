package com.pigeon_stargram.sns_clone.controller.login;

import com.pigeon_stargram.sns_clone.config.auth.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.NewUserEmail;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.login.request.LoginDto;
import com.pigeon_stargram.sns_clone.dto.login.request.RegisterDto;
import com.pigeon_stargram.sns_clone.dto.login.response.UserEmailInfoDto;
import com.pigeon_stargram.sns_clone.service.user.UserService;
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

    private final UserService userService;
    private final HttpSession httpSession;

    @GetMapping("/user-info")
    public UserEmailInfoDto getCurrentMemberEmail(@NewUserEmail String email){
        return new UserEmailInfoDto(email);
    }
    @PostMapping("/register")
    public void register(@RequestBody RegisterDto request){
        userService.save(request);
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto request) {
        User user = userService.login(request);

        if (user != null) {
            log.info("login success");
            httpSession.setAttribute("user", new SessionUser(user));
            return ResponseEntity.ok(new SessionUser(user));
        } else {
            log.info("login fail");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }
    }
    @PostMapping("/logout")
    public void logout() {
        httpSession.invalidate();
    }

}
