package com.pigeon_stargram.sns_clone.controller.user;

import com.pigeon_stargram.sns_clone.config.auth.annotation.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.user.response.LoginUserDto;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/account")
@RestController
public class UserController {

    private final UserService userService;
    @GetMapping("/me")
    public LoginUserDto getCurrentMember(@LoginUser SessionUser loginUser){
        User user = userService.findById(loginUser.getId());
        return new LoginUserDto(user);
    }

}
