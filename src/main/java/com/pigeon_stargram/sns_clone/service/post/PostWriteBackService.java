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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.domain.post.PostFactory.createPostLike;


/**
 * 게시물의 좋아요 사용자 정보를 Redis 캐시와 데이터베이스 간에 동기화하는 서비스 클래스입니다.
 * <p>
 * 이 서비스는 Redis 캐시에서 게시물의 좋아요 사용자 ID를 조회하고, 데이터베이스와 동기화하여
 * 데이터베이스에 누락된 좋아요 정보를 저장합니다.
 * </p>
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PostWriteBackService {

    private final RedisService redisService;
    private final UserService userService;
    private final PostService postService;

    private final PostLikeRepository postLikeRepository;

    /**
     * Redis 캐시에서 게시물의 좋아요 사용자 ID를 조회하고, 데이터베이스와 동기화합니다.
     * <p>
     * 캐시에서 게시물의 좋아요 사용자 ID를 가져와서 데이터베이스에 저장되지 않은 좋아요 정보를
     * 데이터베이스에 추가합니다.
     * </p>
     * @param key Redis 캐시의 키
     */
    public void syncPostLikeUserIds(String key) {
        Long postId = RedisUtil.parseSuffix(key);

        List<Long> cachePostLikeUserIds = redisService.getSetAsLongListExcludeDummy(key);

        // 데이터베이스에 저장되지 않은 좋아요 사용자 ID를 찾아서 데이터베이스에 저장합니다.
        cachePostLikeUserIds.stream()
                .filter(userId -> !postLikeRepository.existsByUserIdAndPostId(userId, postId))
                .forEach(userId -> savePostLike(userId, postId));
    }

    /**
     * 사용자 ID와 게시물 ID를 기반으로 좋아요 정보를 데이터베이스에 저장합니다.
     * <p>
     * 사용자 ID와 게시물 ID로 `PostLike` 객체를 생성하고, 이를 데이터베이스에 저장합니다.
     * </p>
     * @param userId 사용자 ID
     * @param postId 게시물 ID
     */
    private void savePostLike(Long userId, Long postId) {
        PostLike postLike = getPostLike(userId, postId);
        postLikeRepository.save(postLike);
    }

    /**
     * 사용자 ID와 게시물 ID를 기반으로 `PostLike` 객체를 생성합니다.
     * <p>
     * 사용자 ID와 게시물 ID를 통해 `User`와 `Post` 객체를 조회한 후, 이를 사용하여 `PostLike`
     * 객체를 생성합니다.
     * </p>
     * @param postLikeUserId 사용자 ID
     * @param postId 게시물 ID
     * @return 생성된 `PostLike` 객체
     */
    private PostLike getPostLike(Long postLikeUserId,
                                 Long postId) {
        User postLikeUser = userService.getUserById(postLikeUserId);
        Post post = postService.findById(postId);

        return createPostLike(postLikeUser, post);
    }
}
