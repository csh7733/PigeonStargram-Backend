package com.pigeon_stargram.sns_clone.service.post;

import com.pigeon_stargram.sns_clone.domain.post.Post;
import com.pigeon_stargram.sns_clone.domain.post.PostLike;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.repository.post.PostLikeRepository;
import com.pigeon_stargram.sns_clone.repository.post.PostRepository;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.*;
import static com.pigeon_stargram.sns_clone.service.post.PostBuilder.*;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.*;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PostWriteBackService {

    private final RedisService redisService;
    private final UserService userService;
    private final PostService postService;

    private final PostLikeRepository postLikeRepository;

    private final String postLikeUserIdsPattern = cacheKeyPatternGenerator(POST_LIKE_USER_IDS, POST_ID);

    public void writeBackPostLikeUserIds() {
        List<String> postLikeUserIdsKeys = redisService.findKeyByPattern(postLikeUserIdsPattern);

        postLikeUserIdsKeys.forEach(this::syncPostLikeUserIds);
    }

    private void syncPostLikeUserIds(String key) {
        Long postId = parsePostId(key);

        List<Long> postLikeUserIds = redisService.getSetAsLongListExcludeDummy(key);
        log.info("WriteBack key={}", key);
        List<Long> list = postLikeRepository.findByPostId(postId).stream()
                .map(PostLike::getUser)
                .map(User::getId)
                .toList();
        log.info("before={}", list);
        log.info("after={}", postLikeUserIds);

        postLikeUserIds.forEach(postLikeUserId -> {
            PostLike postLike = getPostLike(postLikeUserId, postId);
            postLikeRepository.save(postLike);
        });
    }

    private PostLike getPostLike(Long postLikeUserId, Long postId) {
        User postLikeUser = userService.findById(postLikeUserId);
        Post post = postService.findById(postId);

        return buildPostLike(postLikeUser, post);
    }

    private static Long parsePostId(String key) {
        String[] parts = key.split("_", 2);
        return Long.valueOf(parts[1].trim());
    }
}
