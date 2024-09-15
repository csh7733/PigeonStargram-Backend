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

/**
 * {@link PostCrudService}를 구현한 게시물 CRUD 서비스 클래스입니다.
 * <p>
 * 이 클래스는 게시물에 대한 CRUD 작업을 수행하며, Redis 캐시와 데이터베이스를 연동하여
 * 게시물 정보를 효율적으로 관리합니다. 캐시를 활용하여 조회 성능을 향상시키고,
 * 데이터베이스와의 일관성을 유지합니다.
 * </p>
 */
// Value     | Structure | Key                | FieldKey
// --------- | --------- | ------------------ | --------
// post      | String    | POST               |           JSON 직렬화된 Post객체
// postId    | Set       | ALL_POST_IDS       |           사용자의 모든 게시물 ID
// postId    | Set       | RECENT_POST_IDS    |           사용자의 최근 게시물ID
// commentId | Set       | ALL_COMMENT_IDS    |           게시물의 모든 댓글 ID
// userId    | Set       | POST_LIKE_USER_IDS |           게시물을 좋아하는 사용자 ID
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PostCrudServiceV2 implements PostCrudService {

    private final RedisService redisService;

    private final PostRepository repository;

    @Cacheable(value = POST,
            key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).POST_ID + '_' + #postId")
    @Override
    public Post findById(Long postId) {
        return repository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(POST_NOT_FOUND_ID));
    }

    @Override
    public List<Long> findPostIdByUserId(Long userId) {
        // 사용자 ID를 기반으로 캐시 키를 생성합니다
        String cacheKey = cacheKeyGenerator(ALL_POST_IDS, USER_ID, userId.toString());

        // 캐시에서 게시물 ID 목록을 찾습니다.
        if (redisService.hasKey(cacheKey)) {
            return redisService.getSetAsLongListExcludeDummy(cacheKey);
        }

        // 캐시에 게시물 ID 목록이 없으면 데이터베이스에서 조회합니다.
        List<Long> postIds = getPostIdFromRepository(userId);

        // 조회된 게시물 ID 목록을 캐시에 저장하고 반환합니다.
        return redisService.cacheListToSetWithDummy(postIds, cacheKey, ONE_DAY_TTL);
    }

    @Override
    public List<Long> findPostIdsByUserIdAndCreatedDateAfter(Long userId,
                                                             LocalDateTime createdDate) {
        // 사용자 ID를 기반으로 캐시 키를 생성합니다.
        String cacheKey = cacheKeyGenerator(RECENT_POST_IDS, USER_ID, userId.toString());

        // 캐시에서 최근 게시물 ID 목록을 찾습니다.
        if (redisService.hasKey(cacheKey)) {
            return redisService.getSetAsLongListExcludeDummy(cacheKey);
        }

        // 캐시에 게시물 ID 목록이 없으면 데이터베이스에서 조회합니다.
        List<Long> postIds = getPostIdFromRepository(userId, createdDate);

        // 조회된 최근 게시물 ID 목록을 캐시에 저장하고 반환합니다.
        return redisService.cacheListToSetWithDummy(postIds, cacheKey, ONE_DAY_TTL);
    }

    @CachePut(value = POST,
            key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).POST_ID + '_' + #post.id")
    @Override
    public Post save(Post post) {
        // 게시물을 데이터베이스에 저장합니다.
        Post savedPost = repository.save(post);

        Long postUserId = post.getUser().getId();

        // 사용자의 모든 게시물 ID 캐시에 반영합니다.
        String allPostIdsKey = cacheKeyGenerator(ALL_POST_IDS, USER_ID, postUserId.toString());
        if (redisService.hasKey(allPostIdsKey)) {
            redisService.addToSet(allPostIdsKey, post.getId(), ONE_DAY_TTL);
        }

        // 사용자의 최근 게시물 ID 캐시에 반영합니다.
        String recentPostIdsKey = cacheKeyGenerator(RECENT_POST_IDS, USER_ID, postUserId.toString());
        if (redisService.hasKey(recentPostIdsKey)) {
            redisService.addToSet(recentPostIdsKey, post.getId(), ONE_DAY_TTL);
        }

        return savedPost;
    }

    @CachePut(value = POST,
            key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).POST_ID + '_' + #postId")
    @Override
    public Post edit(Long postId,
                     String newContent) {
        // 게시물 ID로 데이터베이스에서 게시물을 조회합니다.
        Post post = repository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(POST_NOT_FOUND_ID));
        // 게시물의 내용을 수정합니다.
        post.editContent(newContent);

        return post;
    }

    @CachePut(value = POST,
            key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).POST_ID + '_' + #post.id")
    @Override
    public Post updateImage(Post post) {
        // 저장된 이미지를 캐시에 반영합니다.
        return post;
    }

    @CacheEvict(value = POST,
            key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).POST_ID + '_' + #postId")
    @Override
    public void deleteById(Long postId) {
        Long postUserId = findById(postId).getUser().getId();
        // 게시물을 데이터베이스에서 삭제합니다.
        repository.deleteById(postId);

        // 모든 게시물 ID 캐시에서 게시물 ID를 제거합니다.
        String allPostIdsKeys = cacheKeyGenerator(ALL_POST_IDS, USER_ID, postUserId.toString());
        if (redisService.hasKey(allPostIdsKeys)) {
            redisService.removeFromSet(allPostIdsKeys, postId);
        }

        // 최근 게시물 ID 캐시에서 게시물 ID를 제거합니다.
        String recentPostIdsKeys = cacheKeyGenerator(RECENT_POST_IDS, USER_ID, postUserId.toString());
        if (redisService.hasKey(recentPostIdsKeys)) {
            redisService.removeFromSet(recentPostIdsKeys, postId);
        }

        // 댓글 ID 캐시를 제거합니다.
        String allCommentIdsKeys = cacheKeyGenerator(ALL_COMMENT_IDS, POST_ID, postId.toString());
        if (redisService.hasKey(allCommentIdsKeys)) {
            redisService.removeSet(allCommentIdsKeys);
        }

        // 게시물 좋아요 사용자 ID 캐시를 제거합니다.
        String postLikeUserIdsKeys = cacheKeyGenerator(POST_LIKE_USER_IDS, POST_ID, postId.toString());
        if (redisService.hasKey(postLikeUserIdsKeys)) {
            redisService.removeSet(postLikeUserIdsKeys);
        }
    }

    /**
     * 사용자 ID를 기준으로 게시물 ID 목록을 데이터베이스에서 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 게시물 ID 리스트
     */
    private List<Long> getPostIdFromRepository(Long userId) {
        return repository.findByUserId(userId).stream()
                .map(Post::getId)
                .collect(Collectors.toList());
    }

    /**
     * 사용자 ID와 작성 날짜를 기준으로 게시물 ID 목록을 데이터베이스에서 조회합니다.
     *
     * @param userId      사용자 ID
     * @param createdDate 게시물 작성 날짜
     * @return 게시물 ID 리스트
     */
    private List<Long> getPostIdFromRepository(Long userId,
                                               LocalDateTime createdDate) {
        return repository.findByUserIdAndCreatedDateAfter(userId, createdDate).stream()
                .map(Post::getId)
                .collect(Collectors.toList());
    }
}
