package com.pigeon_stargram.sns_clone.service.reply;

import com.pigeon_stargram.sns_clone.domain.reply.Reply;
import com.pigeon_stargram.sns_clone.domain.reply.ReplyLike;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.repository.reply.ReplyLikeRepository;
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
public class ReplyWriteBackService {

    private final RedisService redisService;
    private final UserService userService;
    private final ReplyService replyService;

    private final ReplyLikeRepository replyLikeRepository;

    public void syncReplyLikeUserIds(String key) {
        Long replyId = RedisUtil.parseSuffix(key);
        log.info("WriteBack key={}", key);

        //for test
        Set<Long> repositoryReplyLikeUserIds = replyLikeRepository.findByReplyId(replyId).stream()
                .map(ReplyLike::getUser)
                .map(User::getId)
                .collect(Collectors.toSet());
        Set<Long> cacheReplyLikeUserIds = redisService.getSetAsLongListExcludeDummy(key).stream()
                .collect(Collectors.toSet());
        log.info("before={}", repositoryReplyLikeUserIds);
        log.info("after={}", cacheReplyLikeUserIds);

        cacheReplyLikeUserIds.stream()
                .filter(userId -> !replyLikeRepository.existsByUserIdAndReplyId(userId, replyId))
                .forEach(userId -> {
                    ReplyLike replyLike = getReplyLike(userId, replyId);
                    replyLikeRepository.save(replyLike);
                });
    }

    private ReplyLike getReplyLike(Long replyLikeUserId,
                                   Long replyId) {
        User replyLikeUser = userService.getUserById(replyLikeUserId);
        Reply reply = replyService.findById(replyId);

        return ReplyBuilder.buildReplyLike(replyLikeUser, reply);
    }
}
