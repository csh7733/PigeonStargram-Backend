package com.pigeon_stargram.sns_clone.service.timeline.implV2;

import com.pigeon_stargram.sns_clone.domain.post.Post;
import com.pigeon_stargram.sns_clone.dto.Follow.response.ResponseFollowerDto;
import com.pigeon_stargram.sns_clone.dto.post.response.ResponsePostDto;
import com.pigeon_stargram.sns_clone.dto.redis.ScoreWithValue;
import com.pigeon_stargram.sns_clone.exception.post.PostNotFoundException;
import com.pigeon_stargram.sns_clone.service.follow.FollowService;
import com.pigeon_stargram.sns_clone.service.post.PostService;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import com.pigeon_stargram.sns_clone.service.timeline.TimelineService;
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

// 타임라인 서비스에 대한 캐싱을 적용한 TimelineService 구현체
// Value    | Structure  | Key
// -------- | ---------- | ----------------------------------
// timeline | Sorted Set | TIMELINE_USER_ID_{userId}           (게시물 ID와 스코어)
// posts    | Set        | UPLOADING_POSTS_SET                 (업로드 중인 게시물 ID)
@Service
@RequiredArgsConstructor
@Slf4j
public class TimelineServiceV2 implements TimelineService {

    private final PostService postService;
    private final FollowService followService;
    private final RedisService redisService;

    @Transactional
    public List<ResponsePostDto> getFollowingUsersRecentPosts(Long userId) {
        // 1. 유명인 팔로우 대상의 게시물 가져오기
        List<ResponsePostDto> famousPosts = getFamousFollowingsRecentPosts(userId);

        // 2. 비유명인 팔로우 대상의 게시물 가져오기
        List<ResponsePostDto> unFamousPosts = getUnFamousFollowingsRecentPosts(userId);

        // 3. 두 리스트를 합치고, 중복 제거 후 시간 기준으로 정렬하여 반환
        return Stream.concat(famousPosts.stream(), unFamousPosts.stream())
                .collect(Collectors.toMap(
                        ResponsePostDto::getId,  // ID를 키로 사용
                        post -> post,
                        (existing, replacement) -> existing))  // 중복된 ID는 첫 번째 값을 선택
                .values().stream()
                .sorted(Comparator.comparing(
                        post -> post.getProfile().getTime(),
                        getReverseOrderComparator()))
                .collect(Collectors.toList());
    }

    private List<ResponsePostDto> getFamousFollowingsRecentPosts(Long userId) {
        // 유명인은 타임라인 캐시에서가 아니라 최신 게시물들을 직접 조회
        return followService.findFollowings(userId).stream()
                .map(ResponseFollowerDto::getId)
                .filter(followService::isFamousUser) // 유명인만 필터링
                .flatMap(followingId -> postService.getRecentPostsByUser(followingId).stream())
                .collect(Collectors.toList());
    }

    private List<ResponsePostDto> getUnFamousFollowingsRecentPosts(Long userId) {
        // Redis에서 해당 사용자의 타임라인 키 생성
        String timelineKey = cacheKeyGenerator(TIMELINE, USER_ID, userId.toString());

        // 24시간 이전의 시간
        Double expirationTimeMillis = getTimeMillis(getExpirationTime());

        // Redis에서 타임라인에 저장된 모든 게시물 ID와 스코어(타임스탬프)를 가져옴
        List<Long> validPostIds = redisService.getAllFromSortedSetWithScores(timelineKey, Long.class).stream()
                .filter(scoreWithValue -> isValidPost(userId, scoreWithValue, timelineKey, expirationTimeMillis))
                .map(ScoreWithValue::getValue)
                .filter(postId -> !redisService.isMemberOfSet(UPLOADING_POSTS_SET, postId))
                .collect(Collectors.toList());

        // 필터링된 게시물 ID들로 실제 게시물 데이터를 가져오기
        return validPostIds.stream()
                .map(postService::getCombinedPost)
                .collect(Collectors.toList());
    }

    private boolean isValidPost(Long userId, ScoreWithValue<Long> scoreWithValue, String timelineKey, Double expirationTimeMillis) {
        boolean isWithinOneDay = scoreWithValue.getScore() >= expirationTimeMillis;
        Long postId = scoreWithValue.getValue();

        if (!isWithinOneDay) {
            // 24시간 지난 게시물은 Redis에서 제거
            redisService.removeFromSortedSet(timelineKey, postId);
            return false;
        }

        if (!isPostExisting(postId)) {
            // 게시글이 존재하지 않는 경우, Redis에서 제거
            redisService.removeFromSortedSet(timelineKey, postId);
            return false;
        }

        if (!isUserFollowingPostOwner(userId, postId)) {
            // 사용자가 게시글 작성자를 팔로우하지 않는 경우, Redis에서 제거
            redisService.removeFromSortedSet(timelineKey, postId);
            return false;
        }

        return true;
    }

    private boolean isPostExisting(Long postId) {
        return postService.existsById(postId);
    }

    private boolean isUserFollowingPostOwner(Long userId, Long postId) {
        Post post = postService.findById(postId);
        Long postUserId = post.getUser().getId();
        return followService.isFollowing(userId, postUserId);
    }


}
