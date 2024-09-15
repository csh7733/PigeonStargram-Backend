package com.pigeon_stargram.sns_clone.controller.timeline;

import com.pigeon_stargram.sns_clone.config.auth.annotation.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.dto.post.response.ResponsePostDto;
import com.pigeon_stargram.sns_clone.service.timeline.TimelineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 타임라인과 관련된 API 요청을 처리하는 Controller 클래스입니다.
 * 사용자가 팔로우한 사람들의 최근 게시물을 조회하는 기능을 제공합니다.
 * 타임라인 서비스와 연동하여 최신 게시물을 불러옵니다.
 */
@Slf4j
@RequestMapping("/api/timeline")
@RestController
@RequiredArgsConstructor
public class TimelineController {

    private final TimelineService timelineService;

    /**
     * 사용자가 팔로우한 사람들의 최근 게시물을 가져오는 엔드포인트.
     * 타임라인 서비스에서 해당 사용자의 팔로우 목록을 기반으로
     * 최신 게시물을 조회합니다.
     *
     * @param loginUser 현재 로그인한 사용자 (세션 정보)
     * @return 팔로우한 사용자들의 최근 게시물 목록 (ResponsePostDto 리스트)
     */
    @GetMapping
    public List<ResponsePostDto> getFollowingUsersRecentPosts(@LoginUser SessionUser loginUser) {
        Long userId = loginUser.getId();

        return timelineService.getFollowingUsersRecentPosts(userId);
    }

}
