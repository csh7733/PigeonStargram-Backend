package com.pigeon_stargram.sns_clone.service.post;

import com.pigeon_stargram.sns_clone.domain.post.Post;
import com.pigeon_stargram.sns_clone.domain.post.PostLike;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.repository.post.PostLikeRepository;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.*;
import static com.pigeon_stargram.sns_clone.service.post.PostBuilder.buildPostLike;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.cacheKeyGenerator;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PostLikeCrudService {

    private final RedisService redisService;

    //write through 를 위한 임시 서비스
    private final UserService userService;
    private final PostCrudService postCrudService;

    private final PostLikeRepository repository;

    public void toggleLike(Long userId,
                           Long postId) {
        String cacheKey = cacheKeyGenerator(POST_LIKE_USER_IDS, POST_ID, postId.toString());

        // 임시 write through를 위한 객체
        User user = userService.findById(userId);
        Post post = postCrudService.findById(postId);

        // 캐시 히트
        if (redisService.hasKey(cacheKey)) {
            log.info("toggleLike = {} 캐시 히트", userId);

            if (redisService.isMemberOfSet(cacheKey, userId)) {
                redisService.removeFromSet(cacheKey, userId);

                // 임시 write through
                repository.delete(buildPostLike(user, post));
            } else {
                redisService.addToSet(cacheKey, userId);

                // 임시 write through
                repository.save(buildPostLike(user, post));
            }

            return;
        }

        // 캐시 미스
        log.info("toggleLike = {} 캐시 미스", postId);
        List<PostLike> postLikes = repository.findByPostId(postId);

        List<Long> userIds =
                postLikes.stream()
                        .map(PostLike::getUser)
                        .map(User::getId)
                        .collect(Collectors.toList());
        // 비어있는 set을 캐시하기 위한 더미데이터
        userIds.add(0L);
        
        if (userIds.contains(userId)) {
            userIds.remove(userId);
        } else {
            userIds.add(userId);
        }

        redisService.addAllToSet(cacheKey, userIds);

        // 임시 write through
        postLikes.stream()
                .filter(postLike -> postLike.getUser().getId().equals(userId))
                .findFirst()
                .ifPresentOrElse(postLike -> {
                    repository.delete(postLike);
                }, () -> {
                    repository.save(buildPostLike(user, post));
                });
    }

    public PostLike save(PostLike postLike) {
        return repository.save(postLike);
    }

    public void delete(PostLike postLike) {
        repository.delete(postLike);
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

        redisService.addAllToSet(cacheKey, postLikeUserIds);

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

        return redisService.cacheListToSetWithDummy(postLikeUserIds, cacheKey);
    }
}
