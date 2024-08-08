package com.pigeon_stargram.sns_clone.controller.follow;

import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.Follow.*;
import com.pigeon_stargram.sns_clone.service.follow.FollowService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/{userId}")
@RestController
public class FollowController {

    private final UserService userService;
    private final FollowService followService;

    // 특정유저가 팔로우중인 사람 조회
    @GetMapping("/following")
    public List<FollowerDto> getFollowing(@PathVariable Long userId) {
        return followService.findFollowings(userId).stream()
                .map(user -> new FollowerDto(user, 1))
                .toList();
    }
    
    // 특정유저를 팔로우중인 조회
    @GetMapping("/followers")
    public List<FollowerDto> getFollowers(@PathVariable Long userId) {
        return followService.findFollowers(userId).stream()
                .map(user -> new FollowerDto(user, 1))
                .toList();
    }

    // 팔로우 추가
    @PostMapping("/follow")
    public List<FollowerDto> addFollower(@PathVariable Long userId) {
        //temp
        User tempUser = userService.findAll().stream().findFirst().get();

        followService.createFollow(new AddFollowDto(tempUser.getId(), userId));
        return getFollowers(userId);
    }

    // 팔로우 삭제
    @DeleteMapping("/follow")
    public List<FollowerDto> deleteFollower(@PathVariable Long userId) {
        //temp
        User tempUser = userService.findAll().stream().findFirst().get();

        followService.deleteFollow(new DeleteFollowDto(tempUser.getId(), userId));
        return getFollowers(userId);
    }


}
