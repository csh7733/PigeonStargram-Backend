package com.pigeon_stargram.sns_clone.service.post;

import com.pigeon_stargram.sns_clone.domain.post.PostLike;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.repository.post.PostLikeRepository;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import com.pigeon_stargram.sns_clone.service.redis.WriteBackScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.*;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.cacheKeyGenerator;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PostLikeCrudService {

    private final RedisService redisService;

    private final PostLikeRepository repository;

    /**
     * Post에 대한 User의 Like 정보를 캐시에서 토글한다.
     * @param userId 좋아요를 누른 사용자 Id
     * @param postId Post Id
     */
    public void toggleLike(Long userId,
                           Long postId) {
        String cacheKey = cacheKeyGenerator(POST_LIKE_USER_IDS, POST_ID, postId.toString());

        // write back set에 추가
        redisService.pushToWriteBackSet(cacheKey);

        // 캐시 히트
        if (redisService.hasKey(cacheKey)) {
            log.info("toggleLike 캐시 히트, postId={}, userId={}", postId, userId);

            // 좋아요 정보 토글
            if (redisService.isMemberOfSet(cacheKey, userId)) {
                redisService.removeFromSet(cacheKey, userId);
            } else {
                redisService.addToSet(cacheKey, userId, ONE_DAY_TTL);
            }

            return;
        }

        // 캐시 미스
        log.info("toggleLike 캐시 미스, postId={}, userId={}", postId, userId);
        List<PostLike> postLikes = repository.findByPostId(postId);

        List<Long> postLikeUserIds = postLikes.stream()
                        .map(PostLike::getUser)
                        .map(User::getId)
                        .collect(Collectors.toList());
        postLikeUserIds.add(0L);

        // 좋아요 정보 토글
        if (postLikeUserIds.contains(userId)) {
            postLikeUserIds.remove(userId);
        } else {
            postLikeUserIds.add(userId);
        }
    }

    public Integer countByPostId(Long postId) {
        // 수동 캐시
        String cacheKey = cacheKeyGenerator(POST_LIKE_USER_IDS, POST_ID, postId.toString());

        if (redisService.hasKey(cacheKey)) {
            log.info("countByPostId {} 캐시 히트", postId);
            return redisService.getSetSize(cacheKey).intValue() - 1;
        }
        log.info("countByPostId {} 캐시 미스", postId);

        // DB 조회후 레디스에 캐시
        List<Long> postLikeUserIds = repository.findByPostId(postId).stream()
                .map(PostLike::getUser)
                .map(User::getId)
                .collect(Collectors.toList());
        postLikeUserIds.add(0L);

        redisService.addAllToSet(cacheKey, postLikeUserIds, ONE_DAY_TTL);

        return postLikeUserIds.size() - 1;
    }

    public List<Long> getPostLikeUserIds(Long postId) {
        // 수동 캐시
        String cacheKey = cacheKeyGenerator(POST_LIKE_USER_IDS, POST_ID, postId.toString());

        if (redisService.hasKey(cacheKey)) {
            log.info("getPostLikeUserIds {} 캐시 히트", postId);
            return redisService.getSetAsLongListExcludeDummy(cacheKey);
        }
        log.info("getPostLikeUserIds {} 캐시 미스", postId);

        // DB 조회후 레디스에 캐시
        List<Long> postLikeUserIds = repository.findByPostId(postId).stream()
                .map(PostLike::getUser)
                .map(User::getId)
                .collect(Collectors.toList());

        return redisService.cacheListToSetWithDummy(postLikeUserIds, cacheKey, ONE_DAY_TTL);
    }
}
