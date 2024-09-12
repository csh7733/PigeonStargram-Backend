package com.pigeon_stargram.sns_clone.service.comment;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.domain.comment.CommentLike;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.repository.comment.CommentLikeRepository;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.*;
import static com.pigeon_stargram.sns_clone.service.comment.CommentBuilder.*;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.cacheKeyGenerator;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class CommentLikeCrudService {

    private final RedisService redisService;

    private final CommentLikeRepository repository;

    public void toggleLike(Long userId,
                           Long commentId) {
        String cacheKey = cacheKeyGenerator(COMMENT_LIKE_USER_IDS, COMMENT_ID, commentId.toString());

        // 캐시 히트
        if (redisService.hasKey(cacheKey)) {
            log.info("toggleLike 캐시 히트, commentId={}, userId={}", commentId, userId);

            // 좋아요 정보 토글
            if (redisService.isMemberOfSet(cacheKey, userId)) {
                redisService.removeFromSet(cacheKey, userId);
                // 삭제시 write through
                repository.deleteByUserIdAndCommentId(userId, commentId);
            } else {
                redisService.addToSet(cacheKey, userId, ONE_DAY_TTL);
                // 생성시 write back
                redisService.pushToWriteBackSortedSet(cacheKey);
            }

            return;
        }

        // 캐시 미스
        log.info("toggleLike 캐시 미스, commentId={}, userId={}", commentId, userId);
        List<CommentLike> commentLikes = repository.findByCommentId(commentId);

        List<Long> commentLikeUserIds = commentLikes.stream()
                        .map(CommentLike::getUser)
                        .map(User::getId)
                        .collect(Collectors.toList());
        // 비어있는 set을 캐시하기 위한 더미데이터
        commentLikeUserIds.add(0L);

        // 좋아요 정보 토글
        if (commentLikeUserIds.contains(userId)) {
            commentLikeUserIds.remove(userId);
            repository.deleteByUserIdAndCommentId(userId, commentId);
        } else {
            commentLikeUserIds.add(userId);
            redisService.pushToWriteBackSortedSet(cacheKey);
        }

        redisService.addAllToSet(cacheKey, commentLikeUserIds, ONE_DAY_TTL);
    }

    public Optional<CommentLike> findByUserIdAndCommentId(Long userId,
                                                          Long commentId) {
        return repository.findByUserIdAndCommentId(userId, commentId);
    }

    public CommentLike save(CommentLike commentLike) {
        return repository.save(commentLike);
    }

    public void delete(CommentLike commentLike) {
        repository.delete(commentLike);
    }

    public Integer countByCommentId(Long commentId) {
        // 수동 캐시
        String cacheKey = cacheKeyGenerator(COMMENT_LIKE_USER_IDS, COMMENT_ID, commentId.toString());

        if (redisService.hasKey(cacheKey)) {
            log.info("countByCommentId {} 캐시 히트", commentId);
            return redisService.getSetSize(cacheKey).intValue() - 1;
        }
        log.info("countByCommentId {} 캐시 미스", commentId);

        // DB 조회후 레디스에 캐시
        List<Long> commentLikeUserIds = repository.findByCommentId(commentId).stream()
                .map(CommentLike::getUser)
                .map(User::getId)
                .collect(Collectors.toList());
        commentLikeUserIds.add(0L);

        redisService.addAllToSet(cacheKey, commentLikeUserIds, ONE_DAY_TTL);

        return commentLikeUserIds.size() - 1;
    }

    public List<Long> getCommentLikeUserIds(Long commentId) {
        // 수동 캐시
        String cacheKey = cacheKeyGenerator(COMMENT_LIKE_USER_IDS, COMMENT_ID, commentId.toString());

        if (redisService.hasKey(cacheKey)) {
            log.info("getCommentLikeUserIds {} 캐시 히트", commentId);
            return redisService.getSetAsLongListExcludeDummy(cacheKey);
        }
        log.info("getCommentLikeUserIds {} 캐시 미스", commentId);

        // DB 조회후 레디스에 캐시
        List<Long> commentLikeUserIds = repository.findByCommentId(commentId).stream()
                .map(CommentLike::getUser)
                .map(User::getId)
                .collect(Collectors.toList());

        return redisService.cacheListToSetWithDummy(commentLikeUserIds, cacheKey, ONE_DAY_TTL);
    }
}
