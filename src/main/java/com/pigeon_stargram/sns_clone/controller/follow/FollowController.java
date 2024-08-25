package com.pigeon_stargram.sns_clone.controller.follow;

import com.pigeon_stargram.sns_clone.config.auth.annotation.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.dto.Follow.internal.*;
import com.pigeon_stargram.sns_clone.dto.Follow.request.RequestAddFollowerDto;
import com.pigeon_stargram.sns_clone.dto.Follow.response.ResponseFollowerDto;
import com.pigeon_stargram.sns_clone.service.follow.FollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.pigeon_stargram.sns_clone.service.follow.FollowBuilder.*;

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
        Long loginUserId = loginUser.getId();
        FindFollowingsDto findFollowingsDto =
                buildFindFollowingsDto(loginUserId, userId);
        return followService.findFollowings(findFollowingsDto);
    }

    @GetMapping("/following/check")
    public Boolean isFollowing(@LoginUser SessionUser loginUser,
                               @RequestParam Long followeeId) {
        Long loginUserId = loginUser.getId();

        return followService.isFollowing(loginUserId, followeeId);
    }
    
    // 로그인 유저가 특정유저를 팔로우중인지 조회
    @GetMapping("/followers")
    public List<ResponseFollowerDto> getFollowers(@LoginUser SessionUser loginUser,
                                                  @RequestParam Long userId) {
        Long loginUserId = loginUser.getId();
        FindFollowersDto findFollowersDto =
                buildFindFollowersDto(loginUserId, userId);

        return followService.findFollowers(findFollowersDto);
    }

    // 팔로우 추가
    @PostMapping("")
    public void addFollower(@LoginUser SessionUser loginUser,
                            @RequestBody RequestAddFollowerDto dto) {
        log.info("user: {}", loginUser.getId());
        Long senderId = loginUser.getId();
        Long recipientId = dto.getId();
        AddFollowDto addFollowDto = buildAddFollowDto(senderId, recipientId);

        followService.createFollow(addFollowDto);
    }

    // 팔로우 삭제
    @DeleteMapping("/{followeeId}")
    public void deleteFollower(@LoginUser SessionUser loginUser,
                               @PathVariable Long followeeId) {
        Long loginUserId = loginUser.getId();
        DeleteFollowDto deleteFollowDto =
                buildDeleteFollowDto(loginUserId, followeeId);
        followService.deleteFollow(deleteFollowDto);
    }

    @GetMapping("/count/followers")
    public Long getFollowersCount(@RequestParam Long userId) {
        return followService.countFollowers(userId);
    }

    @GetMapping("/count/followings")
    public Long getFollowingsCount(@RequestParam Long userId) {
        return followService.countFollowings(userId);
    }

    @GetMapping("/notice")
    public Boolean getNotificationEnabled(@LoginUser SessionUser loginUser,
                                          @RequestParam Long targetUserId) {
        Long loginUserId = loginUser.getId();
        GetNotificationEnabledDto getNotificationEnabledDto =
                buildGetNotificationEnabledDto(loginUserId, targetUserId);

        return followService.getNotificationEnabled(getNotificationEnabledDto);
    }

    @PatchMapping("/notice")
    public void toggleNotificationEnabled(@LoginUser SessionUser loginUser,
                                          @RequestParam Long targetUserId) {
        Long loginUserId = loginUser.getId();
        ToggleNotificationEnabledDto toggleNotificationEnabledDto =
                buildToggleNotificationEnabledDto(loginUserId, targetUserId);

        followService.toggleNotificationEnabled(toggleNotificationEnabledDto);
    }

    @GetMapping("/followings/recent-stories")
    public List<ResponseFollowerDto> getMeAndFollowingsWhoUploadStory(@LoginUser SessionUser loginUser) {
        Long userId = loginUser.getId();

        log.info("id={}", userId);
        return followService.findMeAndFollowingsWithRecentStories(userId);
    }

}
