package com.pigeon_stargram.sns_clone.service.comment;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.domain.comment.CommentLike;
import com.pigeon_stargram.sns_clone.domain.post.PostLike;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.repository.comment.CommentLikeRepository;
import com.pigeon_stargram.sns_clone.service.post.PostCrudService;
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
import static com.pigeon_stargram.sns_clone.service.post.PostBuilder.buildPostLike;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.cacheKeyGenerator;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class CommentLikeCrudService {

    private final RedisService redisService;

    //write through 를 위한 임시 서비스
    private final UserService userService;
    private final CommentCrudService commentCrudService;

    private final CommentLikeRepository repository;



    public void toggleLike(Long userId,
                           Long commentId) {
        String cacheKey = cacheKeyGenerator(COMMENT_LIKE_USER_IDS, COMMENT_ID, commentId.toString());

        // 임시 write through를 위한 객체
        User user = userService.findById(userId);
        Comment comment = commentCrudService.findById(commentId);

        // 캐시 히트
        if (redisService.hasKey(cacheKey)) {
            log.info("toggleLike = {} 캐시 히트", userId);

            if (redisService.isMemberOfSet(cacheKey, userId)) {
                redisService.removeFromSet(cacheKey, userId);

                // 임시 write through
                repository.delete(buildCommentLike(user, comment));
            } else {
                redisService.addToSet(cacheKey, userId);

                // 임시 write through
                repository.save(buildCommentLike(user, comment));
            }

            return;
        }

        // 캐시 미스
        log.info("toggleLike = {} 캐시 미스", commentId);
        List<CommentLike> commentLikes = repository.findByCommentId(commentId);

        List<Long> userIds =
                commentLikes.stream()
                        .map(CommentLike::getUser)
                        .map(User::getId)
                        .collect(Collectors.toList());
        // 비어있는 set을 캐시하기 위한 더미데이터
        userIds.add(0L);

        if (userIds.contains(userId)) {
            userIds.remove(userId);
        } else {
            userIds.add(userId);
        }

        redisService.addAllToSet(cacheKey, userIds);

        // 임시 write through
        commentLikes.stream()
                .filter(commentLike -> commentLike.getUser().getId().equals(userId))
                .findFirst()
                .ifPresentOrElse(commentLike -> {
                    repository.delete(commentLike);
                }, () -> {
                    repository.save(buildCommentLike(user, comment));
                });
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

        redisService.addAllToSet(cacheKey, commentLikeUserIds);

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

        return redisService.cacheListToSetWithDummy(commentLikeUserIds, cacheKey);
    }
}
