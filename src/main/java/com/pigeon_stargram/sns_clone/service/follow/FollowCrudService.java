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

            return redisService.getSet(cacheKey).stream()
                    .filter(replyId -> !replyId.equals(0))
                    .map(replyId -> Long.valueOf((Integer) replyId))
                    .collect(Collectors.toList());
        }

        log.info("findFollowerIds(userId = {}) 캐시 미스", userId);

        List<Long> followerIds = repository.findByRecipientId(userId).stream()
                .map(Follow::getSender)
                .map(User::getId)
                .collect(Collectors.toList());

        return cacheListToSetAndReturn(followerIds, cacheKey);
    }

    public List<Long> findFollowingIds(Long userId) {
        String cacheKey = cacheKeyGenerator(FOLLOWING_IDS, USER_ID, userId.toString());

        if (redisService.hasKey(cacheKey)) {
            log.info("findFollowingIds(userId = {}) 캐시 히트", userId);

            return redisService.getSet(cacheKey).stream()
                    .filter(replyId -> !replyId.equals(0))
                    .map(replyId -> Long.valueOf((Integer) replyId))
                    .collect(Collectors.toList());
        }

        log.info("findFollowingIds(userId = {}) 캐시 미스", userId);


        List<Long> followingIds = repository.findBySenderId(userId).stream()
                .map(Follow::getRecipient)// user 가 캐시되면 userId로 가져오도록 변경예정 - FollowService의 findFollowings에서 캐시로 사용하기 위해
                .map(User::getId)
                .collect(Collectors.toList());

        return cacheListToSetAndReturn(followingIds, cacheKey);
    }

    public List<Long> findNotificationEnabledIds(Long userId) {
        String cacheKey = cacheKeyGenerator(NOTIFICATION_ENABLED_IDS, USER_ID, userId.toString());

        if (redisService.hasKey(cacheKey)) {
            log.info("findNotificationEnabledIds(userId = {}) 캐시 히트", userId);

            return redisService.getSet(cacheKey).stream()
                    .filter(replyId -> !replyId.equals(0))
                    .map(replyId -> Long.valueOf((Integer) replyId))
                    .collect(Collectors.toList());
        }

        log.info("findNotificationEnabledIds(userId = {}) 캐시 미스", userId);

        List<Long> notificationEnabledUserIds = repository.findByRecipientId(userId).stream()
                .filter(Follow::getIsNotificationEnabled)
                .map(Follow::getSender)
                .map(User::getId)
                .collect(Collectors.toList());

        return cacheListToSetAndReturn(notificationEnabledUserIds, cacheKey);
    }

    private List<Long> cacheListToSetAndReturn(List<Long> list, String cacheKey) {
        list.add(0L);
        redisService.addAllToSet(cacheKey, list);

        list.remove(0L);
        return list;
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

    public void toggleNotificationEnabled(Long senderId,
                                          Long recipientId) {
        String cacheKey = cacheKeyGenerator(NOTIFICATION_ENABLED_IDS, USER_ID, recipientId.toString());

        if (redisService.hasKey(cacheKey)) {
            log.info("toggleNotificationEnabled(recipientId = {}) 캐시 히트", recipientId);

            if (redisService.isMemberOfSet(cacheKey, senderId)) {
                redisService.removeFromSet(cacheKey, senderId);
            } else {
                redisService.addToSet(cacheKey, senderId);
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
