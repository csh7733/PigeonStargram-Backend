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

    private final ReplyLikeRepository repository;

    public void toggleLike(Long userId,
                           Long replyId) {
        String cacheKey = cacheKeyGenerator(REPLY_LIKE_USER_IDS, REPLY_ID, replyId.toString());

        // 캐시 히트
        if (redisService.hasKey(cacheKey)) {
            log.info("toggleLike 캐시 히트, replyId={}, userId={}", replyId, userId);

            // 좋아요 정보 토글
            if (redisService.isMemberOfSet(cacheKey, userId)) {
                redisService.removeFromSet(cacheKey, userId);
                // 삭제시 write through
                repository.deleteByUserIdAndReplyId(userId, replyId);
            } else {
                redisService.addToSet(cacheKey, userId, ONE_DAY_TTL);
                // 생성시 write back
                redisService.pushToWriteBackSortedSet(cacheKey);
            }

            return;
        }

        // 캐시 미스
        log.info("toggleLike 캐시 미스, replyId={}, userId={}", replyId, userId);
        List<ReplyLike> replyLikes = repository.findByReplyId(replyId);

        List<Long> replyLikeUserIds = replyLikes.stream()
                        .map(ReplyLike::getUser)
                        .map(User::getId)
                        .collect(Collectors.toList());
        // 비어있는 set을 캐시하기 위한 더미데이터
        replyLikeUserIds.add(0L);

        // 좋아요 정보 토글
        if (replyLikeUserIds.contains(userId)) {
            replyLikeUserIds.remove(userId);
            repository.deleteByUserIdAndReplyId(userId, replyId);
        } else {
            replyLikeUserIds.add(userId);
            redisService.pushToWriteBackSortedSet(cacheKey);
        }

        redisService.addAllToSet(cacheKey, replyLikeUserIds, ONE_DAY_TTL);
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
