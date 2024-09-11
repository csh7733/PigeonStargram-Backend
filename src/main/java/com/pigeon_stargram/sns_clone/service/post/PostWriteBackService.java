package com.pigeon_stargram.sns_clone.service.post;

import com.pigeon_stargram.sns_clone.domain.post.Post;
import com.pigeon_stargram.sns_clone.domain.post.PostLike;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.repository.post.PostLikeRepository;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import com.pigeon_stargram.sns_clone.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.POST_ID;
import static com.pigeon_stargram.sns_clone.constant.CacheConstants.POST_LIKE_USER_IDS;
import static com.pigeon_stargram.sns_clone.service.post.PostBuilder.buildPostLike;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PostWriteBackService {

    private final RedisService redisService;
    private final UserService userService;
    private final PostService postService;

    private final PostLikeRepository postLikeRepository;

    public void syncPostLikeUserIds(String key) {
        Long postId = RedisUtil.parseSuffix(key);
        log.info("WriteBack key={}", key);

        Set<Long> repositoryPostLikeUserIds = postLikeRepository.findByPostId(postId).stream()
                .map(PostLike::getUser)
                .map(User::getId)
                .collect(Collectors.toSet());
        Set<Long> cachePostLikeUserIds = redisService.getSetAsLongListExcludeDummy(key).stream()
                        .collect(Collectors.toSet());
        log.info("before={}", repositoryPostLikeUserIds);
        log.info("after={}", cachePostLikeUserIds);

        removeLike(repositoryPostLikeUserIds, cachePostLikeUserIds);
        createLike(repositoryPostLikeUserIds, cachePostLikeUserIds, postId);
    }

    private void createLike(Set<Long> repositoryPostLikeUserIds, Set<Long> cachePostLikeUserIds, Long postId) {
        Set<Long> createUserIds = new HashSet<>(cachePostLikeUserIds);
        createUserIds.removeAll(repositoryPostLikeUserIds);
        log.info("createUserIds={}", createUserIds);

        createUserIds.forEach(createUserId -> {
            PostLike postLike = getPostLike(createUserId, postId);
            postLikeRepository.save(postLike);
        });
    }

    private void removeLike(Set<Long> repositoryPostLikeUserIds, Set<Long> cachePostLikeUserIds) {
        Set<Long> removeUserIds = new HashSet<>(repositoryPostLikeUserIds);
        removeUserIds.removeAll(cachePostLikeUserIds);
        log.info("removeUserIds={}", removeUserIds);

        postLikeRepository.deleteAllByUserIdIn(removeUserIds);
    }

    private PostLike getPostLike(Long postLikeUserId, Long postId) {
        User postLikeUser = userService.findById(postLikeUserId);
        Post post = postService.findById(postId);

        return buildPostLike(postLikeUser, post);
    }
}
