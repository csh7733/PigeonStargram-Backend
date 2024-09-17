package com.pigeon_stargram.sns_clone.controller.user;

import com.pigeon_stargram.sns_clone.config.auth.annotation.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.user.request.RequestUsernameDto;
import com.pigeon_stargram.sns_clone.dto.user.response.ResponseLoginUserInfoDto;
import com.pigeon_stargram.sns_clone.dto.user.response.ResponseUserInfoDto;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.pigeon_stargram.sns_clone.dto.user.UserDtoConverter.*;
import static com.pigeon_stargram.sns_clone.util.LogUtil.*;

/**
 * 사용자 정보와 관련된 API 요청을 처리하는 Controller 클래스입니다.
 * 사용자 정보 조회 및 회원 정보 처리를 담당합니다.
 */
@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * 현재 로그인한 사용자의 정보를 반환합니다.
     *
     * @param loginUser 현재 로그인한 사용자의 세션 정보 (SessionUser)
     * @return 로그인한 사용자의 상세 정보 (ResponseLoginUserInfoDto)
     */
    @GetMapping("/me")
    public ResponseLoginUserInfoDto getLoginUserInfo(@LoginUser SessionUser loginUser){
        logControllerMethod("getLoginUserInfo", loginUser);

        User user = userService.getUserById(loginUser.getId());
        return toResponseLoginUserInfoDto(user);
    }

    /**
     * 특정 사용자의 ID를 기반으로 해당 사용자의 정보를 반환합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @return 조회된 사용자의 상세 정보 (ResponseUserInfoDto)
     */
    @GetMapping("/{userId}")
    public ResponseUserInfoDto getUserInfo(@PathVariable Long userId){
        logControllerMethod("getUserInfo", userId);

        User user = userService.getUserById(userId);
        return toResponseUserInfoDto(user);
    }

    /**
     * 요청한 사용자 이름을 기준으로 회원 정보를 조회하고 반환합니다.
     *
     * @param request 요청한 사용자의 이름이 담긴 DTO (RequestUsernameDto)
     * @return 조회된 사용자의 정보 (ResponseUserInfoDto)
     */
    @PostMapping
    public ResponseUserInfoDto getUserInfoByName(@RequestBody RequestUsernameDto request){
        logControllerMethod("getUserInfoByName", request);

        User user = userService.getUserByName(request.getName());
        return toResponseUserInfoDto(user);
    }

}
