package com.pigeon_stargram.sns_clone.controller.follow;

import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.Follow.FilterFollowersDto;
import com.pigeon_stargram.sns_clone.dto.Follow.FollowerDto;
import com.pigeon_stargram.sns_clone.service.follow.FollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/followers")
@RestController
public class FollowController {

    private final FollowService followService;

    @GetMapping("/list")
    public List<FollowerDto> getFollowers() {
        return followService.findAll().stream()
                .map(User::toFollowerDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/filter")
    public List<FollowerDto> filterFollowers(@RequestBody FilterFollowersDto dto) {
        return followService.findAll().stream()
                .filter(user -> {
                    String key = dto.getKey();
                    return user.getName().equals(key) ||
                            user.getLocation().equals(key);
                }).map(User::toFollowerDto)
                .collect(Collectors.toList());
    }
}
