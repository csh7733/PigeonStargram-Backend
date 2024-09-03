package com.pigeon_stargram.sns_clone.service.follow;

import com.pigeon_stargram.sns_clone.domain.follow.Follow;
import com.pigeon_stargram.sns_clone.domain.reply.Reply;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.Follow.internal.AddFollowDto;
import com.pigeon_stargram.sns_clone.exception.follow.FollowExistException;
import com.pigeon_stargram.sns_clone.repository.follow.FollowRepository;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.*;
import static com.pigeon_stargram.sns_clone.service.follow.FollowBuilder.buildFollow;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.cacheKeyGenerator;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class FollowCrudService {

    private final RedisService redisService;

    private final FollowRepository repository;

    public List<Long> findFollowers(Long userId) {
        String cacheKey = cacheKeyGenerator(FOLLOWER_IDS, USER_ID, userId.toString());

        if (redisService.hasKey(cacheKey)) {
            log.info("findFollowers(userId = {}) 캐시 히트", userId);

            return redisService.getSet(cacheKey).stream()
                    .filter(replyId -> !replyId.equals(0))
                    .map(replyId -> Long.valueOf((Integer) replyId))
                    .collect(Collectors.toList());
        }

        log.info("findFollowers(userId = {}) 캐시 미스", userId);

        List<Long> followerIds = repository.findByRecipientId(userId).stream()
                .map(Follow::getSender)
                .map(User::getId)
                .collect(Collectors.toList());

        followerIds.add(0L);
        redisService.addAllToSet(cacheKey, followerIds);

        followerIds.remove(0L);
        return followerIds;
    }

    public List<Long> findFollowings(Long userId) {
        String cacheKey = cacheKeyGenerator(FOLLOWING_IDS, USER_ID, userId.toString());

        if (redisService.hasKey(cacheKey)) {
            log.info("findFollowings(userId = {}) 캐시 히트", userId);

            return redisService.getSet(cacheKey).stream()
                    .filter(replyId -> !replyId.equals(0))
                    .map(replyId -> Long.valueOf((Integer) replyId))
                    .collect(Collectors.toList());
        }

        log.info("findFollowings(userId = {}) 캐시 미스", userId);

        List<Long> followingIds = repository.findBySenderId(userId).stream()
                .map(Follow::getRecipient)
                .map(User::getId)
                .collect(Collectors.toList());

        followingIds.add(0L);
        redisService.addAllToSet(cacheKey, followingIds);

        followingIds.remove(0L);
        return followingIds;
    }

    public Follow save(Follow follow) {
        Follow save = repository.save(follow);

        Long senderId = save.getSender().getId();
        Long recipientId = save.getRecipient().getId();

        String followerIds =
                cacheKeyGenerator(FOLLOWER_IDS, USER_ID, recipientId.toString());
        if (redisService.hasKey(followerIds)) {
            log.info("follow 저장후 recipient 에 대한 senderId 캐시 저장 recipientId = {}, senderId = {}",
                    recipientId, senderId);
            redisService.addToSet(followerIds, senderId);
        }

        String followingIds =
                cacheKeyGenerator(FOLLOWING_IDS, USER_ID, senderId.toString());
        if (redisService.hasKey(followingIds)) {
            log.info("follow 저장후 senderId 에 대한 recipientId 캐시 저장 senderId = {}, recipientId = {}",
                    senderId, recipientId);
            redisService.addToSet(followingIds, recipientId);
        }

        return save;
    }

    public void deleteFollowBySenderIdAndRecipientId(Long senderId,
                                                     Long recipientId) {
        repository.deleteBySenderIdAndRecipientId(senderId, recipientId);

        String followerIds =
                cacheKeyGenerator(FOLLOWER_IDS, USER_ID, recipientId.toString());
        if (redisService.hasKey(followerIds)) {
            log.info("follow 삭제후 recipient 에 대한 senderId 캐시 삭제 recipientId = {}, senderId = {}",
                    recipientId, senderId);
            redisService.removeFromSet(followerIds, senderId);
        }

        String followingIds =
                cacheKeyGenerator(FOLLOWING_IDS, USER_ID, senderId.toString());
        if (redisService.hasKey(followingIds)) {
            log.info("follow 삭제후 senderId 에 대한 recipientId 캐시 삭제 senderId = {}, recipientId = {}",
                    senderId, recipientId);
            redisService.removeFromSet(followingIds, recipientId);
        }
    }
}
