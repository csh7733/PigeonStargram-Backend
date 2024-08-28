package com.pigeon_stargram.sns_clone.service.post;

import com.pigeon_stargram.sns_clone.constant.CacheConstants;
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

    @Cacheable(value = ALL_POST_IDS,
            key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).USER_ID + '_' + #userId")
    public List<Integer> findPostIdsByUserId(Long userId) {
        return repository.findByUserId(userId).stream()
                .map(Post::getId)
                .map(Math::toIntExact)
                .collect(Collectors.toList());
    }


    @Cacheable(value = RECENT_POST_IDS,
            key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).USER_ID + '_' + #userId")
    public List<Integer> findPostIdsByUserIdAndCreatedDateAfter(Long userId,
                                                             LocalDateTime createdDate) {
        return repository.findByUserIdAndCreatedDateAfter(userId, createdDate).stream()
                .map(Post::getId)
                .map(Math::toIntExact)
                .collect(Collectors.toList());
    }

    @CachePut(value = POST,
            key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).USER_ID + '_' + #post.id")
    public Post save(Post post) {
        Post save = repository.save(post);

//        redisService.getValueFromHash()

        return
    }

    @CacheEvict(value = POST,
            key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).USER_ID + '_' + #postId")
    public void deleteById(Long postId) {
        repository.deleteById(postId);
    }
}
