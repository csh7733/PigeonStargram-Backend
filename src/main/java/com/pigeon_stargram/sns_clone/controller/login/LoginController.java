package com.pigeon_stargram.sns_clone.controller.login;

import com.pigeon_stargram.sns_clone.config.auth.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.NewUserEmail;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.dto.login.response.UserEmailInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/session")
@RestController
public class LoginController {

    @GetMapping("/user-info")
    public UserEmailInfoDto getCurrentMember(@NewUserEmail String email){
        return new UserEmailInfoDto(email);
    }

}
