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
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
class PostLikeCrudServiceV2 implements PostLikeCrudService{

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
    @Override
    public void toggleLike(Long userId, Long postId) {
        // 캐시 키 생성: 게시물 ID에 기반한 캐시 키를 생성합니다.
        String cacheKey = cacheKeyGenerator(POST_LIKE_USER_IDS, POST_ID, postId.toString());

        // 캐시에서 키의 존재 여부를 확인합니다.
        if (redisService.hasKey(cacheKey)) {

            // 캐시에서 사용자가 이미 좋아요를 누른 상태인지 확인합니다.
            if (redisService.isMemberOfSet(cacheKey, userId)) {
                // 사용자가 이미 좋아요를 누른 경우: 캐시와 데이터베이스에서 좋아요 제거
                redisService.removeFromSet(cacheKey, userId);
                repository.deleteByUserIdAndPostId(userId, postId);
            } else {
                // 사용자가 좋아요를 누르지 않은 경우: 캐시에 좋아요 추가 및 데이터베이스에 write-back
                redisService.addToSet(cacheKey, userId, ONE_DAY_TTL);
                redisService.pushToWriteBackSortedSet(cacheKey);
            }
            return;
        }

        // 캐시 미스: 데이터베이스에서 좋아요 사용자 목록을 조회합니다.
        List<Long> postLikeUserIds = getPostLikeUserIdFromRepositoryWithDummy(postId);

        // 좋아요 정보 토글
        if (postLikeUserIds.contains(userId)) {
            // 사용자가 이미 좋아요를 누른 경우: 목록에서 제거하고 데이터베이스에서 삭제
            postLikeUserIds.remove(userId);
            repository.deleteByUserIdAndPostId(userId, postId);
        } else {
            // 사용자가 좋아요를 누르지 않은 경우: 목록에 추가하고 데이터베이스에 write-back
            postLikeUserIds.add(userId);
            redisService.pushToWriteBackSortedSet(cacheKey);
        }

        // 업데이트된 좋아요 사용자 목록을 캐시에 저장합니다.
        redisService.addAllToSet(cacheKey, postLikeUserIds, ONE_DAY_TTL);
    }

    /**
     * 게시물에 대한 좋아요 개수를 반환합니다.
     * <p>
     * 캐시에서 좋아요 사용자 수를 반환하며, 캐시 미스 시 데이터베이스에서 조회하고 캐시에 저장합니다.
     * </p>
     * @param postId 게시물 ID
     * @return 게시물에 대한 좋아요 개수
     */
    @Override
    public Integer countByPostId(Long postId) {
        // 캐시 키 생성: 게시물 ID에 기반한 캐시 키를 생성합니다.
        String cacheKey = cacheKeyGenerator(POST_LIKE_USER_IDS, POST_ID, postId.toString());

        // 캐시에서 좋아요 개수를 조회합니다.
        if (redisService.hasKey(cacheKey)) {
            return redisService.getSetSize(cacheKey).intValue() - 1;
        }

        // 캐시 미스: 데이터베이스에서 좋아요 사용자 목록을 조회합니다.
        List<Long> postLikeUserIds = getPostLikeUserIdFromRepositoryWithDummy(postId);
        redisService.addAllToSet(cacheKey, postLikeUserIds, ONE_DAY_TTL);

        // 조회된 좋아요 사용자 수를 반환합니다.
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
    @Override
    public List<Long> getPostLikeUserIds(Long postId) {
        // 캐시 키 생성: 게시물 ID에 기반한 캐시 키를 생성합니다.
        String cacheKey = cacheKeyGenerator(POST_LIKE_USER_IDS, POST_ID, postId.toString());

        // 캐시에서 사용자 ID 목록을 조회합니다.
        if (redisService.hasKey(cacheKey)) {
            return redisService.getSetAsLongListExcludeDummy(cacheKey);
        }

        // 캐시 미스: 데이터베이스에서 사용자 ID 목록을 조회합니다.
        List<Long> postLikeUserIds = getPostLikeUserIdFromRepository(postId);

        // 조회된 사용자 ID 목록을 캐시에 저장합니다.
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
        // 비어있는 Set을 캐시하기 위한 더미 데이터를 추가합니다.
        postLikeUserIds.add(0L);
        return postLikeUserIds;
    }
}
