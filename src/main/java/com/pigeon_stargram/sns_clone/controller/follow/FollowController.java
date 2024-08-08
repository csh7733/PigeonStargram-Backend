package com.pigeon_stargram.sns_clone.controller.follow;

import com.pigeon_stargram.sns_clone.config.auth.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.Follow.*;
import com.pigeon_stargram.sns_clone.dto.Follow.request.RequestAddFollowerDto;
import com.pigeon_stargram.sns_clone.service.follow.FollowService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/follow")
@RestController
public class FollowController {

    private final UserService userService;
    private final FollowService followService;

    // 특정유저가 팔로우중인 사람 조회
    @GetMapping("/following")
    public List<FollowerDto> getFollowing(@RequestParam Long userId) {
        return followService.findFollowings(userId);
    }
    
    // 특정유저를 팔로우중인 조회
    @GetMapping("/followers")
    public List<FollowerDto> getFollowers(@RequestParam Long userId) {
        return followService.findFollowers(userId);
    }

    // 팔로우 추가
    @PostMapping("")
    public List<FollowerDto> addFollower(@LoginUser SessionUser user,
                                         @RequestBody RequestAddFollowerDto dto) {
        followService.createFollow(new AddFollowDto(user.getId(), dto.getId()));
        return getFollowers(dto.getId());
    }

    // 팔로우 삭제
    @DeleteMapping("")
    public List<FollowerDto> deleteFollower(@LoginUser SessionUser user,
                                            @RequestBody RequestAddFollowerDto dto) {
        followService.deleteFollow(new DeleteFollowDto(user.getId(), dto.getId()));
        return getFollowers(dto.getId());
    }

}
