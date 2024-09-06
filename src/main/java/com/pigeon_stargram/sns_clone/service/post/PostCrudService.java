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

    public List<Long> findPostIdByUserId(Long userId) {
        String cacheKey = cacheKeyGenerator(ALL_POST_IDS, USER_ID, userId.toString());

        if (redisService.hasKey(cacheKey)) {
            log.info("findPostIdsByUserId = {} 캐시 히트", userId);

            return redisService.getSetAsLongListExcludeDummy(cacheKey);
        }

        log.info("findPostIdsByUserId = {} 캐시 미스", userId);

        List<Long> postIds = repository.findByUserId(userId).stream()
                .map(Post::getId)
                .collect(Collectors.toList());

        return redisService.cacheListToSetWithDummy(postIds, cacheKey);
    }

    public List<Long> findPostIdsByUserIdAndCreatedDateAfter(Long userId,
                                                             LocalDateTime createdDate) {
        String cacheKey = cacheKeyGenerator(RECENT_POST_IDS, USER_ID, userId.toString());

        if (redisService.hasKey(cacheKey)) {
            log.info("findPostIdsByUserIdAncCreatedDateAfter = {} 캐시 히트", userId);

            return redisService.getSetAsLongListExcludeDummy(cacheKey);
        }

        log.info("findPostIdsByUserIdAncCreatedDateAfter = {} 캐시 미스", userId);
        List<Long> postIds =
                repository.findByUserIdAndCreatedDateAfter(userId, createdDate).stream()
                        .map(Post::getId)
                        .collect(Collectors.toList());

        return redisService.cacheListToSetWithDummy(postIds, cacheKey);
    }

    @CachePut(value = POST,
            key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).POST_ID + '_' + #post.id")
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
                cacheKeyGenerator(RECENT_POST_IDS, USER_ID, userId.toString());
        if (redisService.hasKey(recentPostIds)) {
            log.info("post 저장후 userId에 대한 최근 postId 캐시 저장 userId = {}", userId);
            redisService.addToSet(recentPostIds, post.getId());
        }

        return save;
    }

    @CachePut(value = POST,
            key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).POST_ID + '_' + #postId")
    public Post edit(Long postId,
                     String newContent) {
        // 영속화된 post
        Post post = repository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(POST_NOT_FOUND_ID));
        post.modify(newContent);

        return post;
    }

    @CacheEvict(value = POST,
            key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).POST_ID + '_' + #postId")
    public void deleteById(Long postId) {
        // 프록시 문제로 캐시 수동획득으로 수정 필요
        Long postUserId = findById(postId).getUser().getId();
        repository.deleteById(postId);

        String allPostIds =
                cacheKeyGenerator(ALL_POST_IDS, USER_ID, postUserId.toString());
        if (redisService.hasKey(allPostIds)) {
            log.info("post 삭제후 userId에 대한 모든 postId 캐시 삭제 userId = {}", postUserId);
            redisService.removeFromSet(allPostIds, postId);
        }

        String recentPostIds =
                cacheKeyGenerator(RECENT_POST_IDS, USER_ID, postUserId.toString());
        if (redisService.hasKey(recentPostIds)) {
            log.info("post 삭제후 userId에 대한 최근 postId 캐시 삭제 userId = {}", postUserId);
            redisService.removeFromSet(recentPostIds, postId);
        }

        String allCommentIds =
                cacheKeyGenerator(ALL_COMMENT_IDS, POST_ID, postId.toString());
        if (redisService.hasKey(allCommentIds)) {
            log.info("post 삭제후 postId에 대한 commentId 캐시 key 삭제 postId = {}", postId);
            redisService.removeSet(allCommentIds);
        }

        String postLikeUserIds =
                cacheKeyGenerator(POST_LIKE_USER_IDS, POST_ID, postId.toString());
        if (redisService.hasKey(postLikeUserIds)) {
            log.info("post 삭제후 postId에 대한 postLikeUserIds 캐시 삭제 postId = {}", postId);
            redisService.removeSet(postLikeUserIds);
        }
    }

    @CachePut(value = POST,
            key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).POST_ID + '_' + #post.id")
    public Post updateImage(Post post) {
        return post;
    }
}
