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
@RequestMapping("/api/followers")
@RestController
public class FollowController {

    private final UserService userService;
    private final FollowService followService;

    @GetMapping("/list")
    public List<FollowerDto> getFollowers() {
        return followService.findAll().stream()
                .map(user -> new FollowerDto(user, 1))
                .toList();
    }

    @PostMapping("/filter")
    public List<FollowerDto> filterFollowers(@RequestBody RequestFilterFollowersDto dto) {
        //temp
        User tempUser = userService.findAll().stream().findFirst().get();

        return userService.findFollowersByFilter(new FilterFollowersDto(tempUser, dto)).stream()
                .map(user -> new FollowerDto(user, 1))
                .toList();
    }

    @PostMapping("/add")
    public List<FollowerDto> addFollower(@RequestBody RequestAddFollowerDto dto) {
        //temp
        User tempUser = userService.findAll().stream().findFirst().get();
        followService.follow(new AddFollowerDto(tempUser.getId(), dto.getId()));
        return getFollowers();
    }


}
