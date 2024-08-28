package com.pigeon_stargram.sns_clone.service.post;

import com.pigeon_stargram.sns_clone.config.redis.ReadCacheKeyGenerator;
import com.pigeon_stargram.sns_clone.domain.post.PostLike;
import com.pigeon_stargram.sns_clone.repository.post.PostLikeRepository;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PostLikeCrudService {

    private final RedisService redisService;

    private final PostLikeRepository repository;
    private final PostLikeRepository postLikeRepository;

    private final ReadCacheKeyGenerator cacheKeyGenerator;

    public Optional<PostLike> findByUserIdAndPostId(Long userId,
                                                    Long postId) {
        return postLikeRepository.findByUserIdAndPostId(userId, postId);
    }

    public PostLike save(PostLike postLike) {
        return postLikeRepository.save(postLike);
    }

    public void delete(PostLike postLike) {
        postLikeRepository.delete(postLike);
    }

    public Integer countByPostId(Long postId) {
        // 수동 캐시
        String cacheKey = (String) cacheKeyGenerator
                .generate("userId", "countByPostId", List.of(postId));

        if (redisService.hasKey(cacheKey)) {
            log.info("countByPostId {} 캐시 히트", postId);
            return redisService.getSetSize(cacheKey).intValue();
        }
        log.info("countByPostId {} 캐시 미스", postId);

        // 좋아요 0개일때에도 캐시하기위해 빈 자료구조 생성
        redisService.addToSet(cacheKey, "dummy");
        redisService.removeFromSet(cacheKey, "dummy");

        // DB 조회후 레디스에 캐시
        List<PostLike> postLikes = repository.findByPostId(postId);
        postLikes.forEach(postLike -> {
            redisService.addToSet(cacheKey, postLike.getUser().getId());
        });

        return postLikes.size();
    }
}
