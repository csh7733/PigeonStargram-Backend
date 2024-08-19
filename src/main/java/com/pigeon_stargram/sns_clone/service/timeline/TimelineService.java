package com.pigeon_stargram.sns_clone.service.timeline;

import com.pigeon_stargram.sns_clone.domain.follow.Follow;
import com.pigeon_stargram.sns_clone.dto.Follow.ResponseFollowerDto;
import com.pigeon_stargram.sns_clone.dto.post.response.ResponsePostsDto;
import com.pigeon_stargram.sns_clone.service.follow.FollowService;
import com.pigeon_stargram.sns_clone.service.post.PostsService;
import com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.getReverseOrderComparator;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class TimelineService {

    private final PostsService postsService;
    private final FollowService followService;

    public List<ResponsePostsDto> getFollowingUsersRecentPosts(Long userId) {

        return followService.findFollowings(userId).stream()
                .map(ResponseFollowerDto::getId)
                .flatMap(followingId -> postsService.getRecentPostsByUser(followingId).stream())
                .sorted(Comparator.comparing(
                        post-> post.getProfile().getTime(),
                        getReverseOrderComparator()))
                .collect(Collectors.toList());
    }

}
