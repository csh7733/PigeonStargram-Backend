package com.pigeon_stargram.sns_clone.service.post;

import com.pigeon_stargram.sns_clone.domain.post.PostLike;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.repository.post.PostLikeRepository;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.*;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.*;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PostLikeCrudService {

    private final RedisService redisService;

    private final PostLikeRepository repository;
    private final PostLikeRepository postLikeRepository;

    public Optional<PostLike> findByUserIdAndPostId(Long userId,
                                                    Long postId) {
        return postLikeRepository.findByUserIdAndPostId(userId, postId);
    }

    public PostLike save(PostLike postLike) {
        return postLikeRepository.save(postLike);
    }

    public void saveInCache(PostLike postLike) {
        Long postId = postLike.getPost().getId();
        Long userId = postLike.getUser().getId();
        String cacheKey = cacheKeyGenerator(POST_LIKE_USER_IDS, POST_ID, postId.toString());

        redisService.addToSet(cacheKey, userId);
    }

    public void delete(PostLike postLike) {
        postLikeRepository.delete(postLike);
    }

    public void deleteInCache(PostLike postLike) {
        Long postId = postLike.getPost().getId();
        Long userId = postLike.getUser().getId();
        String cacheKey = cacheKeyGenerator(POST_LIKE_USER_IDS, POST_ID, postId.toString());

        redisService.removeFromSet(cacheKey, userId);
    }

    public Integer countByPostId(Long postId) {
        // 수동 캐시
        String cacheKey = cacheKeyGenerator(POST_LIKE_USER_IDS, POST_ID, postId.toString());

        if (redisService.hasKey(cacheKey)) {
            log.info("countByPostId {} 캐시 히트", postId);
            return redisService.getSetSize(cacheKey).intValue();
        }
        log.info("countByPostId {} 캐시 미스", postId);

        // DB 조회후 레디스에 캐시
        List<PostLike> postLikes = repository.findByPostId(postId);

        if (postLikes.isEmpty()) {
            // 좋아요 0개일때에도 캐시하기위해 빈 자료구조 생성
            redisService.addToSet(cacheKey, "dummy");
            redisService.removeFromSet(cacheKey, "dummy");
        } else {
            List<Long> postLikeUserIds = postLikes.stream()
                    .map(PostLike::getUser)
                    .map(User::getId)
                    .toList();
            redisService.addAllToSet(cacheKey, postLikeUserIds);
        }

        return postLikes.size();
    }
}
