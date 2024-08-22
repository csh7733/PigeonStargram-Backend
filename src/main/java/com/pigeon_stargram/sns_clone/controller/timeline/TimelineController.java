package com.pigeon_stargram.sns_clone.controller.timeline;

import com.pigeon_stargram.sns_clone.config.auth.annotation.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.dto.post.response.ResponsePostDto;
import com.pigeon_stargram.sns_clone.service.timeline.TimelineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/api/timeline")
@RestController
@RequiredArgsConstructor
public class TimelineController {

    private final TimelineService timelineService;

    @GetMapping
    public List<ResponsePostDto> getFollowingUsersRecentPosts(@LoginUser SessionUser loginUser) {
        Long userId = loginUser.getId();

        return timelineService.getFollowingUsersRecentPosts(userId);
    }

}
