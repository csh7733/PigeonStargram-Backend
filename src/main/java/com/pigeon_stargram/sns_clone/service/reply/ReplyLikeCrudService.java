package com.pigeon_stargram.sns_clone.service.reply;
import com.pigeon_stargram.sns_clone.domain.reply.Reply;
import com.pigeon_stargram.sns_clone.domain.reply.ReplyLike;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.repository.reply.ReplyLikeRepository;
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
import static com.pigeon_stargram.sns_clone.service.reply.ReplyBuilder.*;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.cacheKeyGenerator;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ReplyLikeCrudService {

    private final RedisService redisService;

    //write through 를 위한 임시 서비스
    private final UserService userService;
    private final ReplyCrudService ReplyCrudService;

    private final ReplyLikeRepository repository;

    public void toggleLike(Long userId,
                           Long replyId) {
        String cacheKey = cacheKeyGenerator(REPLY_LIKE_USER_IDS, REPLY_ID, replyId.toString());

        // 임시 write through를 위한 객체
        User user = userService.findById(userId);
        Reply reply = ReplyCrudService.findById(replyId);

        // 캐시 히트
        if (redisService.hasKey(cacheKey)) {
            log.info("toggleLike = {} 캐시 히트", userId);

            if (redisService.isMemberOfSet(cacheKey, userId)) {
                redisService.removeFromSet(cacheKey, userId);

                // 임시 write through
                repository.delete(buildReplyLike(user, reply));
            } else {
                redisService.addToSet(cacheKey, userId, ONE_DAY_TTL);

                // 임시 write through
                repository.save(buildReplyLike(user, reply));
            }

            return;
        }

        // 캐시 미스
        log.info("toggleLike = {} 캐시 미스", replyId);
        List<ReplyLike> replyLikes = repository.findByReplyId(replyId);

        List<Long> userIds =
                replyLikes.stream()
                        .map(ReplyLike::getUser)
                        .map(User::getId)
                        .collect(Collectors.toList());
        // 비어있는 set을 캐시하기 위한 더미데이터
        userIds.add(0L);

        if (userIds.contains(userId)) {
            userIds.remove(userId);
        } else {
            userIds.add(userId);
        }

        redisService.addAllToSet(cacheKey, userIds, ONE_DAY_TTL);

        // 임시 write through
        replyLikes.stream()
                .filter(replyLike -> replyLike.getUser().getId().equals(userId))
                .findFirst()
                .ifPresentOrElse(replyLike -> {
                    repository.delete(replyLike);
                }, () -> {
                    repository.save(buildReplyLike(user, reply));
                });
    }

    public Optional<ReplyLike> findByUserIdAndReplyId(Long userId,
                                                      Long replyId) {
        return repository.findByUserIdAndReplyId(userId, replyId);
    }

    public ReplyLike save(ReplyLike replyLike) {
        return repository.save(replyLike);
    }

    public void delete(ReplyLike replyLike) {
        repository.delete(replyLike);
    }

    public Integer countByReplyId(Long replyId) {
        // 수동 캐시
        String cacheKey = cacheKeyGenerator(REPLY_LIKE_USER_IDS, REPLY_ID, replyId.toString());

        if (redisService.hasKey(cacheKey)) {
            log.info("countByReplyId {} 캐시 히트", replyId);
            return redisService.getSetSize(cacheKey).intValue() - 1;
        }
        log.info("countByReplyId {} 캐시 미스", replyId);

        // DB 조회후 레디스에 캐시
        List<Long> replyLikeUserIds = repository.findByReplyId(replyId).stream()
                .map(ReplyLike::getUser)
                .map(User::getId)
                .collect(Collectors.toList());
        replyLikeUserIds.add(0L);

        redisService.addAllToSet(cacheKey, replyLikeUserIds, ONE_DAY_TTL);

        return replyLikeUserIds.size() - 1;
    }


    public List<Long> getReplyLikeUserIds(Long replyId) {
        // 수동 캐시
        String cacheKey = cacheKeyGenerator(REPLY_LIKE_USER_IDS, REPLY_ID, replyId.toString());

        if (redisService.hasKey(cacheKey)) {
            log.info("getReplyLikeUserIds {} 캐시 히트", replyId);
            return redisService.getSetAsLongListExcludeDummy(cacheKey);
        }
        log.info("getReplyLikeUserIds {} 캐시 미스", replyId);

        // DB 조회후 레디스에 캐시
        List<Long> replyLikeUserIds = repository.findByReplyId(replyId).stream()
                .map(ReplyLike::getUser)
                .map(User::getId)
                .collect(Collectors.toList());

        return redisService.cacheListToSetWithDummy(replyLikeUserIds, cacheKey, ONE_DAY_TTL);
    }
}
