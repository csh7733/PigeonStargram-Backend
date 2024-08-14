package com.pigeon_stargram.sns_clone.controller.follow;

import com.pigeon_stargram.sns_clone.config.auth.annotation.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.dto.Follow.*;
import com.pigeon_stargram.sns_clone.dto.Follow.request.RequestAddFollowerDto;
import com.pigeon_stargram.sns_clone.dto.Follow.request.RequestDeleteFollowerDto;
import com.pigeon_stargram.sns_clone.service.follow.FollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/follow")
@RestController
public class FollowController {

    private final FollowService followService;

    // 특정유저가 팔로우중인 사람 조회 - 일반
    @GetMapping("/followings")
    public List<ResponseFollowerDto> getFollowing(@LoginUser SessionUser loginUser,
                                                  @RequestParam Long userId) {
        Long currentUserId = loginUser.getId();

        return followService.findFollowings(currentUserId,userId);
    }

    // 특정유저를 팔로우중인 사람 조회 - 채팅
//    @GetMapping("/following")
//    public List<FollowerDto> getFollowing(@RequestParam Long userId) {
//        return followService.findFollowings(userId);
//    }
    
    // 특정유저를 팔로우중인 조회
    @GetMapping("/followers")
    public List<ResponseFollowerDto> getFollowers(@LoginUser SessionUser loginUser,
                                                  @RequestParam Long userId) {
        Long currentUserId = loginUser.getId();

        return followService.findFollowers(currentUserId,userId);
    }

    // 팔로우 추가
    @PostMapping("")
    public List<ResponseFollowerDto> addFollower(@LoginUser SessionUser loginUser,
                                                 @RequestBody RequestAddFollowerDto dto) {
        log.info("user: {}", loginUser.getId());
        followService.createFollow(new AddFollowDto(loginUser.getId(), dto.getId()));
//        return getFollowers(dto.getId());
        return null;
    }

    // 팔로우 삭제
    @DeleteMapping("/{followeeId}")
    public List<ResponseFollowerDto> deleteFollower(@LoginUser SessionUser loginUser,
                                                    @PathVariable Long followeeId) {
        followService.deleteFollow(new DeleteFollowDto(loginUser.getId(), followeeId));
//        return getFollowers(dto.getId());
        return null;
    }

}
