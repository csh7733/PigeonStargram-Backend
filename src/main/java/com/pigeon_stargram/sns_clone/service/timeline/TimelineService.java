package com.pigeon_stargram.sns_clone.service.timeline;

import com.pigeon_stargram.sns_clone.dto.Follow.response.ResponseFollowerDto;
import com.pigeon_stargram.sns_clone.dto.post.response.ResponsePostDto;
import com.pigeon_stargram.sns_clone.dto.redis.ScoreWithValue;
import com.pigeon_stargram.sns_clone.service.follow.FollowServiceV2;
import com.pigeon_stargram.sns_clone.service.post.PostService;
import com.pigeon_stargram.sns_clone.service.post.PostServiceV2;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.TIMELINE;
import static com.pigeon_stargram.sns_clone.constant.CacheConstants.USER_ID;
import static com.pigeon_stargram.sns_clone.constant.RedisPostConstants.UPLOADING_POSTS_SET;
import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.*;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.cacheKeyGenerator;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class TimelineService {

    private final PostService postService;
    private final FollowServiceV2 followService;
    private final RedisService redisService;

    public List<ResponsePostDto> getFollowingUsersRecentPosts(Long userId) {
        // 1. 유명인 팔로우 대상의 게시물 가져오기
        List<ResponsePostDto> famousPosts = getFamousFollowingsRecentPosts(userId);

        // 2. 비유명인 팔로우 대상의 게시물 가져오기
        List<ResponsePostDto> unFamousPosts = getUnFamousFollowingsRecentPosts(userId);

        // 3. 두 리스트를 합치고, 시간 기준으로 정렬하여 반환
        return Stream.concat(famousPosts.stream(), unFamousPosts.stream())
                .sorted(Comparator.comparing(
                        post -> post.getProfile().getTime(),
                        getReverseOrderComparator()))
                .collect(Collectors.toList());
    }


    private List<ResponsePostDto> getFamousFollowingsRecentPosts(Long userId) {
        return followService.findFollowings(userId).stream()
                .map(ResponseFollowerDto::getId)
                .filter(followService::isFamousUser) // 유명인만 필터링
                .flatMap(followingId -> postService.getRecentPostsByUser(followingId).stream())
                .collect(Collectors.toList());
    }

    private List<ResponsePostDto> getUnFamousFollowingsRecentPosts(Long userId) {
        // Redis에서 해당 사용자의 타임라인 키 생성
        String timelineKey = cacheKeyGenerator(TIMELINE, USER_ID, userId.toString());

        // Redis에서 타임라인에 저장된 모든 게시물 ID와 스코어(타임스탬프)를 가져옴
        List<ScoreWithValue<Long>> postIdWithScores = redisService.getAllFromSortedSetWithScores(timelineKey, Long.class);

        // 24시간 이전의 시간
        Double expirationTimeMillis = getTimeMillis(getExpirationTime());

        // 24시간 이내의 게시물만 필터링, 24시간이 지난 게시물은 Redis에서 제거
        List<Long> validPostIds = postIdWithScores.stream()
                .filter(scoreWithValue -> {
                    boolean isWithinOneDay = scoreWithValue.getScore() >= expirationTimeMillis;
                    if (!isWithinOneDay) {
                        // 24시간 지난 게시물은 Redis에서 제거
                        redisService.removeFromSortedSet(timelineKey, scoreWithValue.getValue());
                    }
                    return isWithinOneDay;
                })
                .map(ScoreWithValue::getValue)
                .filter(postId -> !redisService.isMemberOfSet(UPLOADING_POSTS_SET, postId))
                .collect(Collectors.toList());

        // 필터링된 게시물 ID들로 실제 게시물 데이터를 가져오기
        return validPostIds.stream()
                .map(postService::getCombinedPost)
                .collect(Collectors.toList());
    }



}
