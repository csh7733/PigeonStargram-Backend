package com.pigeon_stargram.sns_clone.service.comment;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.exception.comment.CommentNotFoundException;
import com.pigeon_stargram.sns_clone.repository.comment.CommentRepository;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.*;
import static com.pigeon_stargram.sns_clone.constant.PageConstants.*;
import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.COMMENT_NOT_FOUND_ID;
import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.*;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.cacheKeyGenerator;

/**
 * {@link CommentCrudService}를 구현한 댓글 CRUD 서비스 클래스입니다.
 * <p>
 * 이 클래스는 댓글에 대한 CRUD 작업을 수행하며, Redis 캐시와 데이터베이스를 연동하여
 * 댓글 정보를 효율적으로 관리합니다. 캐시를 활용하여 조회 성능을 향상시키고,
 * 데이터베이스와의 일관성을 유지합니다.
 * </p>
 */
// Value     | Structure | Key                   | FieldKey
// --------- | --------- | --------------------- | --------
// comment   | String    | COMMENT               |           JSON 직렬화된 Comment객체
// commentId | Set       | ALL_COMMENT_IDS       |           게시물의 모든 댓글 ID
// replyId   | Set       | ALL_REPLY_IDS         |           댓글의 모든 답글 ID
// userId    | Set       | COMMENT_LIKE_USER_IDS |           댓글을 좋아하는 사용자 ID
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommentCrudServiceV2 implements CommentCrudService{

    private final RedisService redisService;

    private final CommentRepository repository;

    @Cacheable(value = COMMENT,
            key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).COMMENT_ID + '_' + #commentId")
    public Comment findById(Long commentId) {
        return repository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(COMMENT_NOT_FOUND_ID));
    }

    public List<Long> findCommentIdByPostId(Long postId) {
        // 게시물 ID를 기반으로 캐시 키를 생성합니다.
        String cacheKey = cacheKeyGenerator(ALL_COMMENT_IDS, POST_ID, postId.toString());

        // 캐시에서 댓글 ID 목록을 조회합니다.
        if (redisService.hasKey(cacheKey)) {
            return redisService.getRangeByScoreAsList(cacheKey, Double.MIN_VALUE, Double.MAX_VALUE);
        }

        // 캐시에 댓글 ID 목록이 없으면 데이터베이스에서 조회합니다.
        List<ZSetOperations.TypedTuple<Object>> commentIdTypedTuples = getCommentTuples(postId);
        // 조회된 댓글 ID 목록을 캐시에 저장합니다.
        redisService.cacheListToSortedSetWithDummy(commentIdTypedTuples, cacheKey);

        return redisService.getRangeByScoreAsList(cacheKey, Double.MIN_VALUE, Double.MAX_VALUE);
    }

    public List<Long> findCommentIdByPostIdAndCommentId(Long postId,
                                                        Long commentId) {
        // 게시물 ID를 기반으로 캐시 키를 생성합니다.
        String cacheKey = cacheKeyGenerator(ALL_COMMENT_IDS, POST_ID, postId.toString());

        // 캐시에서 이후 댓글 ID 목록을 조회합니다.
        if (redisService.hasKey(cacheKey)) {
            return redisService.getSortedSetAfterValueAsList(cacheKey, commentId);
        }

        // 캐시에 댓글 ID 목록이 없으면 데이터베이스에서 조회합니다.
        List<ZSetOperations.TypedTuple<Object>> commentIdTypedTuples = getCommentTuples(postId);
        // 조회된 이후 댓글 ID 목록을 캐시에 저장합니다.
        redisService.cacheListToSortedSetWithDummy(commentIdTypedTuples, cacheKey, ONE_DAY_TTL);
        // 캐시에서 이후 댓글 ID 목록을 반환합니다.
        return redisService.getSortedSetAfterValueAsList(cacheKey, commentId);
    }

    @CachePut(value = COMMENT,
            key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).COMMENT_ID + '_' + #comment.id")
    public Comment save(Comment comment) {
        // 댓글을 데이터베이스에 저장합니다.
        Comment savedComment = repository.save(comment);

        Long postId = comment.getPost().getId();
        Long commentId = comment.getId();
        Double score = convertToScore(comment.getCreatedDate());

        // 게시물 ID를 기반으로 댓글 ID 목록 캐시를 업데이트합니다.
        String allCommentIdsKey =
                cacheKeyGenerator(ALL_COMMENT_IDS, POST_ID, postId.toString());
        if (redisService.hasKey(allCommentIdsKey)) {
            redisService.addToSortedSet(allCommentIdsKey, score, commentId, ONE_DAY_TTL);
        }

        return savedComment;
    }

    @CachePut(value = COMMENT,
            key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).COMMENT_ID + '_' + #commentId")
    public Comment edit(Long commentId,
                        String newContent) {
        // 댓글 ID로 데이터베이스에서 댓글을 조회합니다.
        Comment comment = repository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(COMMENT_NOT_FOUND_ID));
        // 댓글의 내용을 수정합니다.
        comment.editContent(newContent);

        return comment;
    }

    @CacheEvict(value = COMMENT,
            key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).COMMENT_ID + '_' + #commentId")
    public void deleteById(Long commentId) {
        Long postId = findById(commentId).getPost().getId();
        repository.deleteById(commentId);

        // 댓글 ID가 포함된 모든 캐시를 제거합니다.
        String allCommentIdsKeys =
                cacheKeyGenerator(ALL_COMMENT_IDS, POST_ID, postId.toString());
        if (redisService.hasKey(allCommentIdsKeys)) {
            redisService.removeFromSortedSet(allCommentIdsKeys, commentId);
        }
    }

    public Boolean getIsMoreComments(Long postId,
                                     Long lastCommentId) {
        String cacheKey = cacheKeyGenerator(ALL_COMMENT_IDS, POST_ID, postId.toString());

        if (redisService.hasKey(cacheKey)) {
            return isMoreComment(lastCommentId, cacheKey);
        }

        List<ZSetOperations.TypedTuple<Object>> commentIdTypedTuples = getCommentTuples(postId);

        redisService.cacheListToSortedSetWithDummy(commentIdTypedTuples, cacheKey, ONE_DAY_TTL);

        return isMoreComment(lastCommentId, cacheKey);
    }

    /**
     * 게시물 ID를 기준으로 댓글 ID와 점수를 포함하는 튜플 목록을 데이터베이스에서 조회합니다.
     *
     * @param postId 게시물 ID
     * @return 댓글 ID와 점수를 포함하는 튜플 목록
     */
    private List<ZSetOperations.TypedTuple<Object>> getCommentTuples(Long postId) {
        // 게시물 ID를 기준으로 댓글 ID와 점수를 데이터베이스에서 조회합니다.
        return repository.findByPostId(postId).stream()
                .map(CommentCrudServiceV2::getTypedTuple)
                .collect(Collectors.toList());
    }

    /**
     * 댓글을 댓글 ID와 점수를 포함하는 튜플로 변환합니다.
     *
     * @param comment 댓글 객체
     * @return 댓글 ID와 점수를 포함하는 튜플
     */
    private static DefaultTypedTuple<Object> getTypedTuple(Comment comment) {
        // 댓글 생성 시각을 점수로 변환합니다.
        Double score = convertToScore(comment.getCreatedDate());
        // 댓글 ID와 점수를 포함하는 튜플을 반환합니다.
        return new DefaultTypedTuple<Object>(comment.getId(), score);
    }

    /**
     * 주어진 댓글 ID 이후의 댓글 개수를 기반으로 댓글이 더 있는지 확인합니다.
     *
     * @param lastCommentId 마지막 댓글 ID
     * @param cacheKey 캐시 키
     * @return 댓글이 더 있는 경우 true, 그렇지 않으면 false
     */
    private Boolean isMoreComment(Long lastCommentId, String cacheKey) {
        // 캐시에서 마지막 댓글 ID 이후의 댓글 개수를 조회합니다.
        Long count = redisService.countSortedSetAfterValue(cacheKey, lastCommentId);
        // 댓글 개수가 미리 정의된 댓글 개수보다 많은지 확인합니다.
        if(count <= COMMENT_FETCH_NUM){
            return false;
        } else {
            return true;
        }
    }

}
