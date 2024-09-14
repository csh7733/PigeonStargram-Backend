package com.pigeon_stargram.sns_clone.controller.follow;

import com.pigeon_stargram.sns_clone.config.auth.annotation.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.dto.Follow.internal.*;
import com.pigeon_stargram.sns_clone.dto.Follow.request.RequestAddFollowerDto;
import com.pigeon_stargram.sns_clone.dto.Follow.response.ResponseFollowerDto;
import com.pigeon_stargram.sns_clone.service.follow.FollowServiceV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.pigeon_stargram.sns_clone.dto.Follow.FollowDtoConverter.*;
import static com.pigeon_stargram.sns_clone.util.LogUtil.*;

/**
 * FollowController는 팔로우/언팔로우, 팔로우 관계 조회, 알림 설정 등의
 * 팔로우 관련 요청을 처리하는 REST API 컨트롤러입니다.
 *
 * 주요 기능:
 * - 특정 유저의 팔로워 및 팔로잉 목록 조회
 * - 팔로우/언팔로우 기능
 * - 팔로우 알림 설정 및 해제
 * - 팔로우 상태 체크
 * - 팔로우 관련 알림 정보 조회 및 팔로우 수 조회
 *
 * 로그인한 사용자와 관련된 팔로우 상태 정보 및 팔로우 관련 기능들을 제공합니다.
 */
@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
@Slf4j
public class FollowController {

    private final FollowServiceV2 followService;

    /**
     * 특정 유저가 팔로우 중인 사람들의 목록을 조회합니다.
     *
     * @param loginUser 로그인한 사용자 세션 정보
     * @param userId    팔로우 중인 사용자를 조회할 대상 유저의 ID
     * @return 팔로우 중인 사람들의 목록
     */
    @GetMapping("/followings")
    public List<ResponseFollowerDto> getFollowing(@LoginUser SessionUser loginUser,
                                                  @RequestParam Long userId) {
        logControllerMethod("getFollowing", loginUser, userId);

        FindFollowingsDto dto = toFindFollowingsDto(loginUser.getId(), userId);
        return followService.findFollowings(dto);
    }

    /**
     * 특정 유저를 팔로우하는 사람들의 목록을 조회합니다.
     *
     * @param loginUser 로그인한 사용자 세션 정보
     * @param userId    팔로워를 조회할 대상 유저의 ID
     * @return 팔로워들의 목록
     */
    @GetMapping("/followers")
    public List<ResponseFollowerDto> getFollowers(@LoginUser SessionUser loginUser,
                                                  @RequestParam Long userId) {
        logControllerMethod("getFollowers", loginUser, userId);

        FindFollowersDto dto = toFindFollowersDto(loginUser.getId(), userId);
        return followService.findFollowers(dto);
    }

    /**
     * 로그인한 사용자가 특정 유저를 팔로우하고 있는지 여부를 확인합니다.
     *
     * @param loginUser 로그인한 사용자 세션 정보
     * @param followeeId 팔로우 상태를 확인할 대상 유저의 ID
     * @return 팔로우 여부 (true: 팔로우 중, false: 팔로우하지 않음)
     */
    @GetMapping("/following/check")
    public Boolean checkFollowing(@LoginUser SessionUser loginUser,
                                  @RequestParam Long followeeId) {
        logControllerMethod("checkFollowing", loginUser, followeeId);

        return followService.isFollowing(loginUser.getId(), followeeId);
    }

    /**
     * 특정 유저의 팔로워 수를 조회합니다.
     *
     * @param userId 팔로워 수를 조회할 유저의 ID
     * @return 팔로워 수
     */
    @GetMapping("/count/followers")
    public Long getFollowersCount(@RequestParam Long userId) {
        logControllerMethod("getFollowersCount", userId);

        return followService.countFollowers(userId);
    }

    /**
     * 특정 유저가 팔로우 중인 사람들의 수를 조회합니다.
     *
     * @param userId 팔로우 중인 사람들의 수를 조회할 유저의 ID
     * @return 팔로우 중인 사람들의 수
     */
    @GetMapping("/count/followings")
    public Long getFollowingsCount(@RequestParam Long userId) {
        logControllerMethod("getFollowingsCount", userId);

        return followService.countFollowings(userId);
    }

    /**
     * 팔로우 알림 설정 여부를 조회합니다.
     *
     * @param loginUser 로그인한 사용자 세션 정보
     * @param targetUserId 알림 설정 여부를 조회할 대상 유저의 ID
     * @return 알림 설정 여부 (true: 알림 설정됨, false: 알림 설정되지 않음)
     */
    @GetMapping("/notice")
    public Boolean getNotificationEnabled(@LoginUser SessionUser loginUser,
                                          @RequestParam Long targetUserId) {
        logControllerMethod("getFollowingsCount", loginUser, targetUserId);

        GetNotificationEnabledDto dto = toGetNotificationEnabledDto(loginUser.getId(), targetUserId);
        return followService.getNotificationEnabled(dto);
    }

    /**
     * 로그인한 사용자가 특정 유저를 팔로우합니다.
     *
     * @param loginUser 로그인한 사용자 세션 정보
     * @param request 팔로우 요청 DTO
     */
    @PostMapping("")
    public void addFollower(@LoginUser SessionUser loginUser,
                            @RequestBody RequestAddFollowerDto request) {
        logControllerMethod("addFollower", loginUser, request);

        AddFollowDto dto = toAddFollowDto(loginUser.getId(), request.getId());
        followService.createFollow(dto);
    }

    /**
     * 로그인한 사용자가 특정 유저에 대한 팔로우를 취소합니다.
     *
     * @param loginUser 로그인한 사용자 세션 정보
     * @param followeeId 팔로우 취소할 대상 유저의 ID
     */
    @DeleteMapping("/{followeeId}")
    public void deleteFollower(@LoginUser SessionUser loginUser,
                               @PathVariable Long followeeId) {
        logControllerMethod("deleteFollower", loginUser, followeeId);

        DeleteFollowDto dto = toDeleteFollowDto(loginUser.getId(), followeeId);
        followService.deleteFollow(dto);
    }

    /**
     * 팔로우 알림 설정을 토글(켜기/끄기)합니다.
     *
     * @param loginUser 로그인한 사용자 세션 정보
     * @param targetUserId 알림 설정을 토글할 대상 유저의 ID
     */
    @PatchMapping("/notice")
    public void toggleNotificationEnabled(@LoginUser SessionUser loginUser,
                                          @RequestParam Long targetUserId) {
        logControllerMethod("toggleNotificationEnabled", loginUser, targetUserId);

        ToggleNotificationEnabledDto dto = toToggleNotificationEnabledDto(loginUser.getId(), targetUserId);
        followService.toggleNotificationEnabled(dto);
    }

    /**
     * 로그인한 사용자와 해당 사용자가 팔로우 중인 사람들 중 최근 스토리를 업로드한 사람들을 조회합니다.
     *
     * @param loginUser 로그인한 사용자 세션 정보
     * @return 최근 스토리를 업로드한 팔로잉 목록
     */
    @GetMapping("/followings/recent-stories")
    public List<ResponseFollowerDto> getMeAndFollowingsWhoUploadStory(@LoginUser SessionUser loginUser) {
        logControllerMethod("getMeAndFollowingsWhoUploadStory", loginUser);

        return followService.findMeAndFollowingsWithRecentStories(loginUser.getId());
    }

}
