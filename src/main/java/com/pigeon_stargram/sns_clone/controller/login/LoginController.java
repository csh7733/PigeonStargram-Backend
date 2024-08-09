package com.pigeon_stargram.sns_clone.controller.login;

import com.pigeon_stargram.sns_clone.config.auth.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.dto.login.response.LoginUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/account")
@RestController
public class LoginController {
    @GetMapping("/me")
    public LoginUserDto getCurrentMember(@LoginUser SessionUser user){
        return new LoginUserDto(user.getId());
    }
}
