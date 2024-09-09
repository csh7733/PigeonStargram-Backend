package com.pigeon_stargram.sns_clone.service.follow;

import com.pigeon_stargram.sns_clone.domain.follow.Follow;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.exception.follow.FollowNotFoundException;
import com.pigeon_stargram.sns_clone.repository.follow.FollowRepository;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.*;
import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.*;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.cacheKeyGenerator;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class FollowCrudService {

    private final RedisService redisService;

    private final FollowRepository repository;

    public List<Long> findFollowerIds(Long userId) {
        String cacheKey = cacheKeyGenerator(FOLLOWER_IDS, USER_ID, userId.toString());

        if (redisService.hasKey(cacheKey)) {
            log.info("findFollowerIds(userId = {}) 캐시 히트", userId);

            return redisService.getSetAsLongListExcludeDummy(cacheKey);
        }

        log.info("findFollowerIds(userId = {}) 캐시 미스", userId);

        List<Long> followerIds = repository.findByRecipientId(userId).stream()
                .map(Follow::getSender)
                .map(User::getId)
                .collect(Collectors.toList());

        return redisService.cacheListToSetWithDummy(followerIds, cacheKey, ONE_DAY_TTL);
    }

    public List<Long> findFollowingIds(Long userId) {
        String cacheKey = cacheKeyGenerator(FOLLOWING_IDS, USER_ID, userId.toString());

        if (redisService.hasKey(cacheKey)) {
            log.info("findFollowingIds(userId = {}) 캐시 히트", userId);

            return redisService.getSetAsLongListExcludeDummy(cacheKey);
        }

        log.info("findFollowingIds(userId = {}) 캐시 미스", userId);

        List<Long> followingIds = repository.findBySenderId(userId).stream()
                .map(Follow::getRecipient)// user 가 캐시되면 userId로 가져오도록 변경예정 - FollowService의 findFollowings에서 캐시로 사용하기 위해
                .map(User::getId)
                .collect(Collectors.toList());

        return redisService.cacheListToSetWithDummy(followingIds, cacheKey, ONE_DAY_TTL);
    }

    public List<Long> findNotificationEnabledIds(Long userId) {
        String cacheKey = cacheKeyGenerator(NOTIFICATION_ENABLED_IDS, USER_ID, userId.toString());

        if (redisService.hasKey(cacheKey)) {
            log.info("findNotificationEnabledIds(userId = {}) 캐시 히트", userId);

            return redisService.getSetAsLongListExcludeDummy(cacheKey);
        }

        log.info("findNotificationEnabledIds(userId = {}) 캐시 미스", userId);

        List<Long> notificationEnabledUserIds = repository.findByRecipientId(userId).stream()
                .filter(Follow::getIsNotificationEnabled)
                .map(Follow::getSender)
                .map(User::getId)
                .collect(Collectors.toList());

        return redisService.cacheListToSetWithDummy(notificationEnabledUserIds, cacheKey, ONE_DAY_TTL);
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
            redisService.addToSet(followerIds, senderId, ONE_DAY_TTL);
        }

        String followingIds =
                cacheKeyGenerator(FOLLOWING_IDS, USER_ID, senderId.toString());
        if (redisService.hasKey(followingIds)) {
            log.info("follow 저장후 senderId 에 대한 recipientId 캐시 저장 senderId = {}, recipientId = {}",
                    senderId, recipientId);
            redisService.addToSet(followingIds, recipientId, ONE_DAY_TTL);
        }

        return save;
    }

    public void toggleNotificationEnabled(Long senderId,
                                          Long recipientId) {
        String cacheKey = cacheKeyGenerator(NOTIFICATION_ENABLED_IDS, USER_ID, recipientId.toString());

        if (redisService.hasKey(cacheKey)) {
            log.info("toggleNotificationEnabled(recipientId = {}) 캐시 히트", recipientId);

            if (redisService.isMemberOfSet(cacheKey, senderId)) {
                redisService.removeFromSet(cacheKey, senderId);
            } else {
                redisService.addToSet(cacheKey, senderId, ONE_DAY_TTL);
            }
        }

        Follow follow = repository.findBySenderIdAndRecipientId(senderId, recipientId)
                .orElseThrow(() -> new FollowNotFoundException(FOLLOW_NOT_FOUND));
        follow.toggleNotificationEnabled();
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

        String notificationEnabledIds =
                cacheKeyGenerator(NOTIFICATION_ENABLED_IDS, USER_ID, recipientId.toString());
        if (redisService.hasKey(notificationEnabledIds)) {
            log.info("follow 삭제후 recipientId 에 대한 notificationEnabledId 캐시 삭제 notificationEnabledId = {}, recipientId = {}",
                    senderId, recipientId);
            redisService.removeFromSet(notificationEnabledIds, senderId);
        }
    }


}
