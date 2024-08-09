package com.pigeon_stargram.sns_clone.controller.login;

import com.pigeon_stargram.sns_clone.config.auth.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.NewUserEmail;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.dto.login.request.RegisterDto;
import com.pigeon_stargram.sns_clone.dto.login.response.UserEmailInfoDto;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/session")
@RestController
public class LoginController {

    private final UserService userService;

    @GetMapping("/user-info")
    public UserEmailInfoDto getCurrentMemberEmail(@NewUserEmail String email){
        return new UserEmailInfoDto(email);
    }
    @PostMapping("/register")
    public void register(@RequestBody RegisterDto request){
        userService.save(request);
    }

}
