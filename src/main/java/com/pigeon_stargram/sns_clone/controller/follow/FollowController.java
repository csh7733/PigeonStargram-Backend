package com.pigeon_stargram.sns_clone.controller.follow;

import com.pigeon_stargram.sns_clone.config.auth.annotation.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.Follow.*;
import com.pigeon_stargram.sns_clone.dto.Follow.request.RequestAddFollowerDto;
import com.pigeon_stargram.sns_clone.dto.Follow.response.ResponseFollowerDto;
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

    private final FollowService followService;
    private final UserService userService;

    // 특정유저가 팔로우중인 사람 조회 - 일반
    @GetMapping("/followings")
    public List<ResponseFollowerDto> getFollowing(@LoginUser SessionUser loginUser,
                                                  @RequestParam Long userId) {
        Long currentUserId = loginUser.getId();

        return followService.findFollowings(currentUserId,userId);
    }

    @GetMapping("/following/check")
    public Boolean isFollowing(@LoginUser SessionUser loginUser, @RequestParam Long followeeId) {
        Long currentUserId = loginUser.getId();
        User currentUser = userService.findById(currentUserId);

        User followeeUser = userService.findById(followeeId);

        return followService.isFollowing(currentUser, followeeUser);
    }


    // 특정유저를 팔로우중인 사람 조회 - 채팅
//    @GetMapping("/following")
//    public List<FollowerDto> getFollowing(@RequestParam Long userId) {
//        return followService.findFollowings(userId);
//    }
    
    // 로그인 유저가 특정유저를 팔로우중인지 조회
    @GetMapping("/followers")
    public List<ResponseFollowerDto> getFollowers(@LoginUser SessionUser loginUser,
                                                  @RequestParam Long userId) {
        Long currentUserId = loginUser.getId();

        return followService.findFollowers(currentUserId, userId);
    }

    // 팔로우 추가
    @PostMapping("")
    public void addFollower(@LoginUser SessionUser loginUser,
                                                 @RequestBody RequestAddFollowerDto dto) {
        log.info("user: {}", loginUser.getId());
        followService.createFollow(new AddFollowDto(loginUser.getId(), dto.getId()));
    }

    // 팔로우 삭제
    @DeleteMapping("/{followeeId}")
    public void deleteFollower(@LoginUser SessionUser loginUser,
                                                    @PathVariable Long followeeId) {
        followService.deleteFollow(new DeleteFollowDto(loginUser.getId(), followeeId));
    }

    @GetMapping("/count/followers")
    public Long getFollowersCount(@RequestParam Long userId) {
        User user = userService.findById(userId);
        return followService.countFollowers(user);
    }

    @GetMapping("/count/followings")
    public Long getFollowingsCount(@RequestParam Long userId) {
        User user = userService.findById(userId);
        return followService.countFollowings(user);
    }

    @GetMapping("/notice")
    public Boolean getNotificationEnabled(@LoginUser SessionUser loginUser,
                                          @RequestParam Long targetUserId) {
        Long currentUserId = loginUser.getId();
        User currentUser = userService.findById(currentUserId);

        User targetUser = userService.findById(targetUserId);

        return followService.getNotificationEnabled(currentUser,targetUser);
    }

    @PatchMapping("/notice")
    public void toggleNotificationEnabled(@LoginUser SessionUser loginUser,
                                          @RequestParam Long targetUserId) {
        Long currentUserId = loginUser.getId();
        User currentUser = userService.findById(currentUserId);

        User targetUser = userService.findById(targetUserId);

        followService.toggleNotificationEnabled(currentUser, targetUser);
    }

    @GetMapping("/followings/recent-stories")
    public List<ResponseFollowerDto> getMeAndFollowingsWhoUploadStory(@LoginUser SessionUser loginUser) {
        Long userId = loginUser.getId();

        return followService.findMeAndFollowingsWithRecentStories(userId);
    }

}
