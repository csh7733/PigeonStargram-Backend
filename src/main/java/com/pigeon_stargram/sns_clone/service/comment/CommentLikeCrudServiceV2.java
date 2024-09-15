package com.pigeon_stargram.sns_clone.service.comment;

import com.pigeon_stargram.sns_clone.domain.comment.CommentLike;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.repository.comment.CommentLikeRepository;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.*;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.cacheKeyGenerator;

/**
 * 댓글에 대한 좋아요 정보를 관리하는 서비스 클래스입니다.
 * <p>
 * 이 서비스는 Redis 캐시를 활용하여 댓글의 좋아요 사용자 정보를 관리하며, 데이터베이스와
 * 동기화하는 기능을 제공합니다.
 * </p>
 */
// Value  | Structure | Key
// -----  | --------- | --------------------
// userId | Set       | COMMENT_LIKE_USER_IDS
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommentLikeCrudServiceV2 implements CommentLikeCrudService {

    private final RedisService redisService;
    private final CommentLikeRepository repository;

    @Override
    public void toggleLike(Long userId, Long commentId) {
        // 댓글 ID를 기반으로 캐시 키를 생성합니다.
        String cacheKey = cacheKeyGenerator(COMMENT_LIKE_USER_IDS, COMMENT_ID, commentId.toString());

        // 캐시에서 좋아요 정보가 있는지 확인합니다.
        if (redisService.hasKey(cacheKey)) {

            // 사용자 ID가 캐시에 있는 경우(좋아요가 이미 있는 경우)
            if (redisService.isMemberOfSet(cacheKey, userId)) {
                // 캐시에서 사용자 ID를 제거하고, 데이터베이스에서 좋아요 정보를 삭제합니다.
                redisService.removeFromSet(cacheKey, userId);
                repository.deleteByUserIdAndCommentId(userId, commentId);
            } else {
                // 캐시에서 사용자 ID를 추가하고, 데이터베이스에 좋아요 정보를 추가합니다.
                redisService.addToSet(cacheKey, userId, ONE_DAY_TTL);
                redisService.pushToWriteBackSortedSet(cacheKey);
            }

            return;
        }

        // 캐시 미스: 데이터베이스에서 좋아요 사용자 목록을 조회합니다.
        List<Long> commentLikeUserIds = getCommentLikeUserIdFromRepositoryWithDummy(commentId);

        // 좋아요 정보 토글
        if (commentLikeUserIds.contains(userId)) {
            // 사용자 ID가 목록에 있는 경우, 좋아요를 제거하고 데이터베이스에서 삭제합니다.
            commentLikeUserIds.remove(userId);
            repository.deleteByUserIdAndCommentId(userId, commentId);
        } else {
            // 사용자 ID가 목록에 없는 경우, 좋아요를 추가하고 데이터베이스에 추가합니다.
            commentLikeUserIds.add(userId);
            redisService.pushToWriteBackSortedSet(cacheKey);
        }

        // 업데이트된 좋아요 사용자 목록을 캐시에 저장합니다.
        redisService.addAllToSet(cacheKey, commentLikeUserIds, ONE_DAY_TTL);
    }

    @Override
    public Integer countByCommentId(Long commentId) {
        // 댓글 ID를 기반으로 캐시 키를 생성합니다.
        String cacheKey = cacheKeyGenerator(COMMENT_LIKE_USER_IDS, COMMENT_ID, commentId.toString());

        // 캐시에서 좋아요 개수를 조회합니다.
        if (redisService.hasKey(cacheKey)) {
            return redisService.getSetSize(cacheKey).intValue() - 1;
        }

        // 캐시 미스: 데이터베이스에서 좋아요 사용자 목록을 조회합니다.
        List<Long> commentLikeUserIds = getCommentLikeUserIdFromRepositoryWithDummy(commentId);

        // 좋아요 사용자 목록을 캐시에 저장하고 개수를 반환합니다.
        redisService.addAllToSet(cacheKey, commentLikeUserIds, ONE_DAY_TTL);
        return commentLikeUserIds.size() - 1;
    }

    @Override
    public List<Long> getCommentLikeUserIds(Long commentId) {
        // 댓글 ID를 기반으로 캐시 키를 생성합니다.
        String cacheKey = cacheKeyGenerator(COMMENT_LIKE_USER_IDS, COMMENT_ID, commentId.toString());

        // 캐시에서 사용자 ID 목록을 조회합니다.
        if (redisService.hasKey(cacheKey)) {
            return redisService.getSetAsLongListExcludeDummy(cacheKey);
        }

        // 캐시 미스: 데이터베이스에서 사용자 ID 목록을 조회합니다.
        List<Long> commentLikeUserIds = getCommentLikeUserIdFromRepository(commentId);

        // 조회된 사용자 ID 목록을 캐시에 저장합니다.
        return redisService.cacheListToSetWithDummy(commentLikeUserIds, cacheKey, ONE_DAY_TTL);
    }

    /**
     * 댓글 ID를 기반으로 데이터베이스에서 좋아요 사용자 ID 목록을 조회합니다.
     * 빈 목록을 캐시하기 위한 더미 데이터를 추가합니다.
     *
     * @param commentId 댓글의 ID
     * @return 좋아요 사용자 ID 목록(더미 데이터 포함)
     */
    private List<Long> getCommentLikeUserIdFromRepositoryWithDummy(Long commentId) {
        List<Long> commentLikeUserIds = getCommentLikeUserIdFromRepository(commentId);
        // 비어있는 세트를 캐시하기 위해 더미 데이터를 추가합니다.
        commentLikeUserIds.add(0L);
        return commentLikeUserIds;
    }

    /**
     * 댓글 ID를 기반으로 데이터베이스에서 좋아요 사용자 ID 목록을 조회합니다.
     *
     * @param commentId 댓글의 ID
     * @return 좋아요 사용자 ID 목록
     */
    private List<Long> getCommentLikeUserIdFromRepository(Long commentId) {
        return repository.findByCommentId(commentId).stream()
                .map(CommentLike::getUser)
                .map(User::getId)
                .collect(Collectors.toList());
    }
}

