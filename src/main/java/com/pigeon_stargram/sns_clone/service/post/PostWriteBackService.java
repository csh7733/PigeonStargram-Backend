package com.pigeon_stargram.sns_clone.service.post;

import com.pigeon_stargram.sns_clone.domain.post.Post;
import com.pigeon_stargram.sns_clone.domain.post.PostLike;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.repository.post.PostLikeRepository;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.POST_ID;
import static com.pigeon_stargram.sns_clone.constant.CacheConstants.POST_LIKE_USER_IDS;
import static com.pigeon_stargram.sns_clone.service.post.PostBuilder.buildPostLike;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.cacheKeyWildcardPatternGenerator;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PostWriteBackService {

    private final RedisService redisService;
    private final UserService userService;
    private final PostService postService;

    private final PostLikeRepository postLikeRepository;

    // Redis 에서 사용하는 글로벌 와일드카드 패턴으로, 정규표현식이 아님
    private final String postLikeUserIdsPattern = cacheKeyWildcardPatternGenerator(POST_LIKE_USER_IDS, POST_ID);

    private static Long parsePostId(String key) {
        String[] parts = key.split("_", 2);
        return Long.valueOf(parts[1].trim());
    }

    public void writeBackPostLikeUserIds() {
        List<String> postLikeUserIdsKeys = redisService.findKeyByPattern(postLikeUserIdsPattern);

        postLikeUserIdsKeys.forEach(this::syncPostLikeUserIds);
    }

    public void syncPostLikeUserIds(String key) {
        Long postId = parsePostId(key);

        List<Long> postLikeUserIds = redisService.getSetAsLongListExcludeDummy(key);
        log.info("WriteBack key={}", key);
        List<Long> list = postLikeRepository.findByPostId(postId).stream()
                .map(PostLike::getUser)
                .map(User::getId)
                .toList();
        log.info("before={}", list);
        log.info("after={}", postLikeUserIds);

        postLikeUserIds.stream()
                .filter(postLikeUserId -> !postLikeRepository.existsByUserIdAndPostId(postLikeUserId, postId))
                .forEach(postLikeUserId -> {
                    PostLike postLike = getPostLike(postLikeUserId, postId);
                    postLikeRepository.save(postLike);
                });
    }

    private PostLike getPostLike(Long postLikeUserId, Long postId) {
        User postLikeUser = userService.findById(postLikeUserId);
        Post post = postService.findById(postId);

        return buildPostLike(postLikeUser, post);
    }
}
