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
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.*;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.cacheKeyGenerator;

/**
 * 게시물에 대한 좋아요 정보를 관리하는 서비스 클래스입니다.
 * <p>
 * 이 서비스는 Redis 캐시를 활용하여 게시물의 좋아요 사용자 정보를 관리하며, 데이터베이스와
 * 동기화하는 기능을 제공합니다.
 * </p>
 */
// Value  | Structure | Key                  | FieldKey
// -----  | --------- | -------------------- | --------
// postId | Hash      | UPLOADING_POSTS_HASH | fieldKey  임시 postId로 실제 postId를 찾는 용도
// postId | Set       | UPLOADING_POSTS_SET  |           업로드중인 게시물
// postId | Set       | TIMELINE             |           유저에 대한 타임라인 게시물
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PostLikeCrudService {

    private final RedisService redisService;

    private final PostLikeRepository repository;

    /**
     * 사용자가 게시물에 좋아요를 토글합니다.
     * <p>
     * 캐시에서 사용자의 좋아요 정보를 확인하고, 캐시에 따라 좋아요를 추가하거나 제거합니다.
     * 데이터베이스와의 동기화도 수행합니다.
     * </p>
     * @param userId 좋아요를 누른 사용자 ID
     * @param postId 게시물 ID
     */
    public void toggleLike(Long userId,
                           Long postId) {
        String cacheKey = cacheKeyGenerator(POST_LIKE_USER_IDS, POST_ID, postId.toString());

        // 캐시 히트
        if (redisService.hasKey(cacheKey)) {

            // 좋아요 정보 토글
            if (redisService.isMemberOfSet(cacheKey, userId)) {
                redisService.removeFromSet(cacheKey, userId);
                // 삭제시 write through
                repository.deleteByUserIdAndPostId(userId, postId);
            } else {
                redisService.addToSet(cacheKey, userId, ONE_DAY_TTL);
                // 생성시 write back
                redisService.pushToWriteBackSortedSet(cacheKey);
            }
            return;
        }

        // 캐시 미스
        List<Long> postLikeUserIds = getPostLikeUserIdFromRepositoryWithDummy(postId);

        // 좋아요 정보 토글
        if (postLikeUserIds.contains(userId)) {
            postLikeUserIds.remove(userId);
            repository.deleteByUserIdAndPostId(userId, postId);
        } else {
            postLikeUserIds.add(userId);
            redisService.pushToWriteBackSortedSet(cacheKey);
        }

        redisService.addAllToSet(cacheKey, postLikeUserIds, ONE_DAY_TTL);
    }

    /**
     * 게시물에 대한 좋아요 사용자 수를 반환합니다.
     * <p>
     * 캐시에서 좋아요 사용자 수를 반환하며, 캐시 미스 시 데이터베이스에서 조회하고 캐시에 저장합니다.
     * </p>
     * @param postId 게시물 ID
     * @return 게시물에 대한 좋아요 사용자 수
     */
    public Integer countByPostId(Long postId) {
        String cacheKey = cacheKeyGenerator(POST_LIKE_USER_IDS, POST_ID, postId.toString());

        if (redisService.hasKey(cacheKey)) {
            return redisService.getSetSize(cacheKey).intValue() - 1;
        }

        // DB 조회후 레디스에 캐시
        List<Long> postLikeUserIds = getPostLikeUserIdFromRepositoryWithDummy(postId);

        redisService.addAllToSet(cacheKey, postLikeUserIds, ONE_DAY_TTL);

        return postLikeUserIds.size() - 1;
    }

    /**
     * 게시물에 대한 좋아요 사용자 ID 목록을 반환합니다.
     * <p>
     * 캐시에서 사용자 ID 목록을 반환하며, 캐시 미스 시 데이터베이스에서 조회하고 캐시에 저장합니다.
     * </p>
     * @param postId 게시물 ID
     * @return 게시물에 대한 좋아요 사용자 ID 목록
     */
    public List<Long> getPostLikeUserIds(Long postId) {
        // 수동 캐시
        String cacheKey = cacheKeyGenerator(POST_LIKE_USER_IDS, POST_ID, postId.toString());

        if (redisService.hasKey(cacheKey)) {
            return redisService.getSetAsLongListExcludeDummy(cacheKey);
        }

        // DB 조회후 레디스에 캐시
        List<Long> postLikeUserIds = getPostLikeUserIdFromRepository(postId);

        return redisService.cacheListToSetWithDummy(postLikeUserIds, cacheKey, ONE_DAY_TTL);
    }

    /**
     * 데이터베이스에서 게시물에 대한 좋아요 사용자 ID 목록을 조회합니다.
     * <p>
     * 조회된 ID 목록에 더미 데이터를 추가하여 반환합니다.
     * </p>
     * @param postId 게시물 ID
     * @return 게시물에 대한 좋아요 사용자 ID 목록
     */
    private List<Long> getPostLikeUserIdFromRepository(Long postId) {
        return repository.findByPostId(postId).stream()
                .map(PostLike::getUser)
                .map(User::getId)
                .collect(Collectors.toList());
    }

    /**
     * 데이터베이스에서 게시물에 대한 좋아요 사용자 ID 목록을 조회하고,
     * 비어 있는 Set을 캐시하기 위한 더미 데이터를 추가합니다.
     * @param postId 게시물 ID
     * @return 게시물에 대한 좋아요 사용자 ID 목록 (더미 데이터 포함)
     */
    private List<Long> getPostLikeUserIdFromRepositoryWithDummy(Long postId) {
        List<Long> postLikeUserIds = getPostLikeUserIdFromRepository(postId);
        // 비어있는 set을 캐시하기 위한 더미데이터
        postLikeUserIds.add(0L);
        return postLikeUserIds;
    }
}
