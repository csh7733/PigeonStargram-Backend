package com.pigeon_stargram.sns_clone.service.comment;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.exception.comment.CommentNotFoundException;
import com.pigeon_stargram.sns_clone.repository.comment.CommentRepository;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.*;
import static com.pigeon_stargram.sns_clone.constant.PageConstants.*;
import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.COMMENT_NOT_FOUND_ID;
import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.*;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.cacheKeyGenerator;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class CommentCrudService {

    private final RedisService redisService;

    private final CommentRepository repository;

    @Cacheable(value = COMMENT,
            key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).COMMENT_ID + '_' + #commentId")
    public Comment findById(Long commentId) {
        return repository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(COMMENT_NOT_FOUND_ID));
    }

    public List<Long> findCommentIdByPostId(Long postId) {
        String cacheKey = cacheKeyGenerator(ALL_COMMENT_IDS, POST_ID, postId.toString());

        if (redisService.hasKey(cacheKey)) {
            log.info("findCommentIdsByPostId = {} 캐시 히트", postId);

            return redisService.getRangeByScore(cacheKey, Double.MIN_VALUE, Double.MAX_VALUE).stream()
                    .map(value -> Long.valueOf((Integer) value))
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.toList());
        }

        log.info("findCommentIdsByPostId = {} 캐시 미스", postId);

        List<ZSetOperations.TypedTuple<Object>> commentIdTypedTuples = repository.findByPostId(postId).stream()
                .map(comment -> {
                    Double score = convertToScore(comment.getCreatedDate());
                    return new DefaultTypedTuple<Object>(comment.getId(), score);
                })
                .collect(Collectors.toList());

        redisService.cacheListToSortedSetWithDummy(commentIdTypedTuples, cacheKey);

        return redisService.getRangeByScore(cacheKey, Double.MIN_VALUE, Double.MAX_VALUE).stream()
                .map(value -> Long.valueOf((Integer) value))
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    public List<Long> findCommentIdByPostIdAndCommentId(Long postId,
                                                        Long commentId) {
        String cacheKey = cacheKeyGenerator(ALL_COMMENT_IDS, POST_ID, postId.toString());

        if (redisService.hasKey(cacheKey)) {
            log.info("findCommentIdByPostIdByPage = {}, commentId = {} 캐시 히트", postId, commentId);

            return redisService.getSortedSetAfterValueAsList(cacheKey, commentId);
        }

        log.info("findCommentIdByPostIdByPage = {}, commentId = {} 캐시 미스", postId, commentId);
        List<ZSetOperations.TypedTuple<Object>> commentIdTypedTuples = repository.findByPostId(postId).stream()
                .map(comment -> {
                    Double score = convertToScore(comment.getCreatedDate());
                    return new DefaultTypedTuple<Object>(comment.getId(), score);
                })
                .collect(Collectors.toList());

        redisService.cacheListToSortedSetWithDummy(commentIdTypedTuples, cacheKey);

        return redisService.getSortedSetAfterValueAsList(cacheKey, commentId);
    }

    @CachePut(value = COMMENT,
            key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).COMMENT_ID + '_' + #comment.id")
    public Comment save(Comment comment) {
        Comment save = repository.save(comment);

        Long postId = comment.getPost().getId();
        Long commentId = comment.getId();
        Double score = convertToScore(comment.getCreatedDate());

        String allCommentIds =
                cacheKeyGenerator(ALL_COMMENT_IDS, POST_ID, postId.toString());
        if (redisService.hasKey(allCommentIds)) {
            log.info("comment 저장후 postId에 대한 모든 commentId 캐시 저장 commentId = {}", postId);
            redisService.addToSortedSet(allCommentIds, score, commentId);
        }

        return save;
    }

    @CachePut(value = COMMENT,
            key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).COMMENT_ID + '_' + #commentId")
    public Comment edit(Long commentId,
                        String newContent) {
        // 영속화된 comment
        Comment comment = repository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(COMMENT_NOT_FOUND_ID));
        comment.modify(newContent);

        return comment;
    }

    @CacheEvict(value = COMMENT,
            key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).COMMENT_ID + '_' + #commentId")
    public void deleteById(Long commentId) {
        // 프록시 문제로 캐시 수동획득으로 수정 필요
        Long postId = findById(commentId).getPost().getId();
        repository.deleteById(commentId);

        String allCommentIds =
                cacheKeyGenerator(ALL_COMMENT_IDS, POST_ID, postId.toString());
        if (redisService.hasKey(allCommentIds)) {
            log.info("comment 삭제후 postId에 대한 모든 commentId 캐시 삭제 postId = {}", postId);
            redisService.removeFromSet(allCommentIds, commentId);
        }

        String recentCommentIds =
                cacheKeyGenerator(RECENT_COMMENT_IDS, POST_ID, postId.toString());
        if (redisService.hasKey(recentCommentIds)) {
            log.info("comment 삭제후 postId에 대한 최근 commentId 캐시 삭제 postId = {}", postId);
            redisService.removeFromSet(recentCommentIds, commentId);
        }

        String allReplyIds =
                cacheKeyGenerator(ALL_REPLY_IDS, COMMENT_ID, commentId.toString());
        if (redisService.hasKey(allReplyIds)) {
            log.info("comment 삭제후 commentId에 대한 replyId 캐시 key 삭제 commentId = {}", commentId);
            redisService.removeSet(allReplyIds);
        }

        String commentLikeUserIds =
                cacheKeyGenerator(COMMENT_LIKE_USER_IDS, COMMENT_ID, commentId.toString());
        if (redisService.hasKey(commentLikeUserIds)) {
            log.info("comment 삭제후 commentId에 대한 commentLikeUserIds 캐시 삭제 commentId = {}", commentId);
            redisService.removeSet(commentLikeUserIds);
        }
    }

    public Boolean getIsMoreComment(Long postId,
                                    Long lastCommentId) {
        String cacheKey = cacheKeyGenerator(ALL_COMMENT_IDS, POST_ID, postId.toString());

        if (redisService.hasKey(cacheKey)) {
            log.info("getIsMoreComment = {}, lastCommentId = {} 캐시 히트", postId, lastCommentId);

            return isMoreComment(lastCommentId, cacheKey);
        }

        log.info("getIsMoreComment = {}, lastCommentId = {} 캐시 미스", postId, lastCommentId);
        List<ZSetOperations.TypedTuple<Object>> commentIdTypedTuples = repository.findByPostId(postId).stream()
                .map(comment -> {
                    Double score = convertToScore(comment.getCreatedDate());
                    return new DefaultTypedTuple<Object>(comment.getId(), score);
                })
                .collect(Collectors.toList());

        redisService.cacheListToSortedSetWithDummy(commentIdTypedTuples, cacheKey);

        return isMoreComment(lastCommentId, cacheKey);
    }

    private Boolean isMoreComment(Long lastCommentId, String cacheKey) {
        Long count = redisService.countSortedSetAfterValue(cacheKey, lastCommentId);
        if(count < COMMENT_FETCH_NUM){
            return false;
        } else {
            return true;
        }
    }

}
