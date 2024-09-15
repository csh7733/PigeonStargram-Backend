package com.pigeon_stargram.sns_clone.service.comment.implV2;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.domain.comment.CommentFactory;
import com.pigeon_stargram.sns_clone.domain.comment.CommentLike;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.repository.comment.CommentLikeRepository;
import com.pigeon_stargram.sns_clone.service.comment.CommentService;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import com.pigeon_stargram.sns_clone.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.pigeon_stargram.sns_clone.domain.comment.CommentFactory.*;

/**
 * 댓글의 좋아요 사용자 정보를 Redis 캐시와 데이터베이스 간에 동기화하는 서비스 클래스입니다.
 * <p>
 * 이 서비스는 Redis 캐시에서 댓글의 좋아요 사용자 ID를 조회하고, 데이터베이스와 동기화하여
 * 데이터베이스에 누락된 좋아요 정보를 저장합니다.
 * </p>
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommentWriteBackService {

    private final RedisService redisService;
    private final UserService userService;
    private final CommentService commentService;
    private final CommentLikeRepository commentLikeRepository;

    /**
     * Redis 캐시에서 댓글의 좋아요 사용자 ID를 조회하고, 데이터베이스와 동기화합니다.
     * <p>
     * 캐시에서 댓글의 좋아요 사용자 ID를 가져와서 데이터베이스에 저장되지 않은 좋아요 정보를
     * 데이터베이스에 추가합니다.
     * </p>
     * @param key Redis 캐시의 키
     */
    public void syncCommentLikeUserIds(String key) {
        Long commentId = RedisUtil.parseSuffix(key);

        List<Long> cacheCommentLikeUserIds = redisService.getSetAsLongListExcludeDummy(key);

        // 데이터베이스에 저장되지 않은 좋아요 사용자 ID를 찾아서 데이터베이스에 저장합니다.
        cacheCommentLikeUserIds.stream()
                .filter(userId -> !commentLikeRepository.existsByUserIdAndCommentId(userId, commentId))
                .forEach(userId -> saveCommentLike(userId, commentId));
    }

    /**
     * 사용자 ID와 댓글 ID를 기반으로 좋아요 정보를 데이터베이스에 저장합니다.
     * <p>
     * 사용자 ID와 댓글 ID로 `CommentLike` 객체를 생성하고, 이를 데이터베이스에 저장합니다.
     * </p>
     * @param userId 사용자 ID
     * @param commentId 댓글 ID
     */
    private void saveCommentLike(Long userId, Long commentId) {
        CommentLike commentLike = getCommentLike(userId, commentId);
        commentLikeRepository.save(commentLike);
    }

    /**
     * 사용자 ID와 댓글 ID를 기반으로 `CommentLike` 객체를 생성합니다.
     * <p>
     * 사용자 ID와 댓글 ID를 통해 `User`와 `Comment` 객체를 조회한 후, 이를 사용하여 `CommentLike`
     * 객체를 생성합니다.
     * </p>
     * @param commentLikeUserId 사용자 ID
     * @param commentId 댓글 ID
     * @return 생성된 `CommentLike` 객체
     */
    private CommentLike getCommentLike(Long commentLikeUserId, Long commentId) {
        User commentLikeUser = userService.getUserById(commentLikeUserId);
        Comment comment = commentService.findById(commentId);

        return createCommentLike(commentLikeUser, comment);
    }
}
