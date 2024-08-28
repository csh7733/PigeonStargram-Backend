package com.pigeon_stargram.sns_clone.service.post;

import com.pigeon_stargram.sns_clone.domain.post.Post;
import com.pigeon_stargram.sns_clone.exception.post.PostNotFoundException;
import com.pigeon_stargram.sns_clone.repository.post.PostRepository;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.*;
import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.POST_NOT_FOUND_ID;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.cacheKeyGenerator;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PostCrudService {

    private final RedisService redisService;

    private final PostRepository repository;

    @Cacheable(value = POST,
            key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).POST_ID + '_' + #postId")
    public Post findById(Long postId) {
        return repository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(POST_NOT_FOUND_ID));
    }

    public List<Post> findByUserId(Long userId) {
        return repository.findByUserId(userId);
    }

    public List<Integer> findPostIdsByUserId(Long userId) {
        String cacheKey = cacheKeyGenerator(ALL_POST_IDS, USER_ID, userId.toString());

        if (redisService.hasKey(cacheKey)) {
            log.info("findPostIdsByUserId = {} 캐시 히트", userId);

            return redisService.getSet(cacheKey).stream()
                    .map(postId -> (Integer) postId)
                    .collect(Collectors.toList());
        }

        log.info("findPostIdsByUserId = {} 캐시 미스", userId);
        List<Integer> postIds = repository.findByUserId(userId).stream()
                .map(Post::getId)
                .map(Math::toIntExact)
                .collect(Collectors.toList());

        if (postIds.isEmpty()) {
            redisService.addToSet(cacheKey, "dummy");
            redisService.removeFromSet(cacheKey, "dummy");
        } else {
            redisService.addAllToSet(cacheKey, postIds);
        }

        return postIds;
    }

    public List<Integer> findPostIdsByUserIdAndCreatedDateAfter(Long userId,
                                                                LocalDateTime createdDate) {
        String cacheKey = cacheKeyGenerator(RECENT_POST_IDS, USER_ID, userId.toString());

        if (redisService.hasKey(cacheKey)) {
            log.info("findPostIdsByUserIdAncCreatedDateAfter = {} 캐시 히트", userId);

            return redisService.getSet(cacheKey).stream()
                    .map(postId -> (Integer) postId)
                    .collect(Collectors.toList());
        }

        log.info("findPostIdsByUserIdAncCreatedDateAfter = {} 캐시 미스", userId);
        List<Integer> postIds =
                repository.findByUserIdAndCreatedDateAfter(userId, createdDate).stream()
                        .map(Post::getId)
                        .map(Math::toIntExact)
                        .collect(Collectors.toList());

        if (postIds.isEmpty()) {
            redisService.addToSet(cacheKey, "dummy");
            redisService.removeFromSet(cacheKey, "dummy");
        } else {
            redisService.addAllToSet(cacheKey, postIds);
        }

        return postIds;
    }

    @CachePut(value = POST,
            key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).USER_ID + '_' + #post.id")
    public Post save(Post post) {
        Post save = repository.save(post);

        Long userId = post.getUser().getId();

        String allPostIds =
                cacheKeyGenerator(ALL_POST_IDS, USER_ID, userId.toString());
        if (redisService.hasKey(allPostIds)) {
            log.info("post 저장후 userId에 대한 모든 postId 캐시 저장 userId = {}", userId);
            redisService.addToSet(allPostIds, post.getId());
        }

        String recentPostIds =
                cacheKeyGenerator(ALL_POST_IDS, USER_ID, userId.toString());
        if (redisService.hasKey(recentPostIds)) {
            log.info("post 저장후 userId에 대한 최근 postId 캐시 저장 userId = {}", userId);
            redisService.addToSet(recentPostIds, post.getId());
        }

        return save;
    }

    @CacheEvict(value = POST,
            key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).USER_ID + '_' + #postId")
    public void deleteById(Long postId) {
        Long postUserId = findById(postId).getUser().getId();
        repository.deleteById(postId);

        String allPostIds =
                cacheKeyGenerator(ALL_POST_IDS, USER_ID, postUserId.toString());
        if (redisService.hasKey(allPostIds)) {
            log.info("post 삭제후 userId에 대한 모든 postId 캐시 삭제 userId = {}", postUserId);
            redisService.removeFromSet(allPostIds, postId);
        }

        String recentPostIds =
                cacheKeyGenerator(ALL_POST_IDS, USER_ID, postUserId.toString());
        if (redisService.hasKey(recentPostIds)) {
            log.info("post 삭제후 userId에 대한 최근 postId 캐시 삭제 userId = {}", postUserId);
            redisService.removeFromSet(recentPostIds, postId);
        }
    }
}
