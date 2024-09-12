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

        return getFollowerIdsFromDB(userId, cacheKey);
    }

    public List<Long> findFollowingIds(Long userId) {
        String cacheKey = cacheKeyGenerator(FOLLOWING_IDS, USER_ID, userId.toString());

        if (redisService.hasKey(cacheKey)) {
            log.info("findFollowingIds(userId = {}) 캐시 히트", userId);

            return redisService.getSetAsLongListExcludeDummy(cacheKey);
        }

        log.info("findFollowingIds(userId = {}) 캐시 미스", userId);

        return getFollowingIdsFromDB(userId, cacheKey);
    }

    public List<Long> findNotificationEnabledIds(Long userId) {
        String cacheKey = cacheKeyGenerator(NOTIFICATION_ENABLED_IDS, USER_ID, userId.toString());

        if (redisService.hasKey(cacheKey)) {
            log.info("findNotificationEnabledIds(userId = {}) 캐시 히트", userId);

            return redisService.getSetAsLongListExcludeDummy(cacheKey);
        }

        log.info("findNotificationEnabledIds(userId = {}) 캐시 미스", userId);

        return getNotificationEnabledIdsFromDB(userId, cacheKey);
    }

    public void save(Follow follow) {
        Long senderId = follow.getSender().getId();
        Long recipientId = follow.getRecipient().getId();

        // Follower Ids 관련 처리
        String followerIds = cacheKeyGenerator(FOLLOWER_IDS, USER_ID, recipientId.toString());
        // Write-back Sorted Set에 추가
        redisService.pushToWriteBackSortedSet(followerIds);

        // 캐시가 존재하지 않으면 DB에서 가져온다
        if (!redisService.hasKey(followerIds)) {
            getFollowerIdsFromDB(recipientId, followerIds);
        }

        // 캐시에 follower ID 추가
        redisService.addToSet(followerIds, senderId, ONE_DAY_TTL);

        // Following Ids 관련 처리
        String followingIds = cacheKeyGenerator(FOLLOWING_IDS, USER_ID, senderId.toString());
        // Write-back Sorted Set에 추가
        redisService.pushToWriteBackSortedSet(followingIds);

        // 캐시가 존재하지 않으면 DB에서 가져온다
        if (!redisService.hasKey(followingIds)) {
            getFollowingIdsFromDB(senderId, followingIds);
        }

        // 캐시에 following ID 추가
        redisService.addToSet(followingIds, recipientId, ONE_DAY_TTL);
    }

    public void toggleNotificationEnabled(Long senderId, Long recipientId) {
        // 캐시 키 생성 (recipientId를 기준으로 Notification Enabled 상태를 관리)
        String cacheKey = cacheKeyGenerator(NOTIFICATION_ENABLED_IDS, USER_ID, recipientId.toString());

        // 캐시 키를 Write-back Sorted Set에 추가하여 나중에 동기화하도록 준비
        redisService.pushToWriteBackSortedSet(cacheKey);

        // 캐시가 존재하지 않으면 DB에서 Notification Enabled ID 목록을 로드
        if (!redisService.hasKey(cacheKey)) {
            getNotificationEnabledIdsFromDB(recipientId, cacheKey);
        }

        // senderId가 Notification Enabled Set에 이미 존재하면 삭제 (알림 비활성화)
        if (redisService.isMemberOfSet(cacheKey, senderId)) {
            redisService.removeFromSet(cacheKey, senderId);
        } else {
            // 존재하지 않으면 추가 (알림 활성화) 및 TTL 설정
            redisService.addToSet(cacheKey, senderId, ONE_DAY_TTL);
        }
    }


//    Follow follow = repository.findBySenderIdAndRecipientId(senderId, recipientId)
//            .orElseThrow(() -> new FollowNotFoundException(FOLLOW_NOT_FOUND));
//        follow.toggleNotificationEnabled();
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

    private List<Long> getFollowerIdsFromDB(Long userId, String cacheKey) {
        List<Long> followerIds = repository.findByRecipientId(userId).stream()
                .map(Follow::getSender)
                .map(User::getId)
                .collect(Collectors.toList());

        return redisService.cacheListToSetWithDummy(followerIds, cacheKey, ONE_DAY_TTL);
    }

    private List<Long> getFollowingIdsFromDB(Long userId, String cacheKey) {
        List<Long> followingIds = repository.findBySenderId(userId).stream()
                .map(Follow::getRecipient)
                .map(User::getId)
                .collect(Collectors.toList());

        return redisService.cacheListToSetWithDummy(followingIds, cacheKey, ONE_DAY_TTL);
    }

    private List<Long> getNotificationEnabledIdsFromDB(Long userId, String cacheKey) {
        List<Long> notificationEnabledUserIds = repository.findByRecipientId(userId).stream()
                .filter(Follow::getIsNotificationEnabled)
                .map(Follow::getSender)
                .map(User::getId)
                .collect(Collectors.toList());

        return redisService.cacheListToSetWithDummy(notificationEnabledUserIds, cacheKey, ONE_DAY_TTL);
    }




}
