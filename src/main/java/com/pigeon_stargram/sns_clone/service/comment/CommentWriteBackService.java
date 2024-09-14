package com.pigeon_stargram.sns_clone.service.comment;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.domain.comment.CommentLike;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.repository.comment.CommentLikeRepository;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import com.pigeon_stargram.sns_clone.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class CommentWriteBackService {

    private final RedisService redisService;
    private final UserService userService;
    private final CommentService commentService;

    private final CommentLikeRepository commentLikeRepository;

    public void syncCommentLikeUserIds(String key) {
        Long commentId = RedisUtil.parseSuffix(key);
        log.info("WriteBack key={}", key);

        //for test
        Set<Long> repositoryCommentLikeUserIds = commentLikeRepository.findByCommentId(commentId).stream()
                .map(CommentLike::getUser)
                .map(User::getId)
                .collect(Collectors.toSet());
        Set<Long> cacheCommentLikeUserIds = redisService.getSetAsLongListExcludeDummy(key).stream()
                .collect(Collectors.toSet());
        log.info("before={}", repositoryCommentLikeUserIds);
        log.info("after={}", cacheCommentLikeUserIds);

        cacheCommentLikeUserIds.stream()
                .filter(userId -> !commentLikeRepository.existsByUserIdAndCommentId(userId, commentId))
                .forEach(userId -> {
                    CommentLike commentLike = getCommentLike(userId, commentId);
                    commentLikeRepository.save(commentLike);
                });
    }

    private CommentLike getCommentLike(Long commentLikeUserId,
                                       Long commentId) {
        User commentLikeUser = userService.getUserById(commentLikeUserId);
        Comment comment = commentService.findById(commentId);

        return CommentBuilder.buildCommentLike(commentLikeUser, comment);
    }
}
