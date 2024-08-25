package com.pigeon_stargram.sns_clone.controller.user;

import com.pigeon_stargram.sns_clone.config.auth.annotation.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.user.request.RequestCurrentMemberDto;
import com.pigeon_stargram.sns_clone.dto.user.response.ResponseLoginUserDto;
import com.pigeon_stargram.sns_clone.dto.user.response.ResponseUserInfoDto;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.pigeon_stargram.sns_clone.service.user.UserBuilder.buildLoginUserDto;
import static com.pigeon_stargram.sns_clone.service.user.UserBuilder.buildResponseUserInfoDto;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/account")
@RestController
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseLoginUserDto getCurrentMember(@LoginUser SessionUser loginUser){
        User user = userService.findById(loginUser.getId());
        return buildLoginUserDto(user);
    }

    @GetMapping("/{userId}")
    public ResponseUserInfoDto getCurrentMember(@PathVariable Long userId){
        User user = userService.findById(userId);
        log.info("user = " + user.getName());
        return buildResponseUserInfoDto(user);
    }

    @PostMapping
    public ResponseUserInfoDto getCurrentMember(@RequestBody RequestCurrentMemberDto request){
        User user = userService.findByName(request.getName());
        return buildResponseUserInfoDto(user);
    }

}
