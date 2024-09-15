package com.pigeon_stargram.sns_clone.service.follow;

import com.pigeon_stargram.sns_clone.domain.follow.Follow;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.repository.follow.FollowRepository;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.*;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.cacheKeyGenerator;


// 팔로우 관련 CRUD 연산을 처리하는 서비스 클래스입니다.
// Redis 캐시와 데이터베이스를 활용하여 팔로우 정보의 조회 및 업데이트를 수행합니다.
// Value        | Structure | Key
// ------------ | --------- | ---------------
// followerIds  | Set       | FOLLOWER_IDS
// followingIds | Set       | FOLLOWING_IDS
// userIds      | Set       | NOTIFICATION_ENABLED_IDS
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class FollowCrudServiceV2 implements FollowCrudService{

    private final RedisService redisService;
    private final FollowRepository repository;

    public List<Long> findFollowerIds(Long userId) {
        String cacheKey = cacheKeyGenerator(FOLLOWER_IDS, USER_ID, userId.toString());

        // 캐시에서 가져오기
        if (redisService.hasKey(cacheKey)) {
            return redisService.getSetAsLongListExcludeDummy(cacheKey);
        }

        // 캐시 미스 : DB에서 가져오기
        return getFollowerIdsFromDB(userId, cacheKey);
    }

    public List<Long> findFollowingIds(Long userId) {
        String cacheKey = cacheKeyGenerator(FOLLOWING_IDS, USER_ID, userId.toString());

        // 캐시에서 가져오기
        if (redisService.hasKey(cacheKey)) {
            return redisService.getSetAsLongListExcludeDummy(cacheKey);
        }

        // 캐시 미스 : DB에서 가져오기
        return getFollowingIdsFromDB(userId, cacheKey);
    }

    public List<Long> findNotificationEnabledIds(Long userId) {
        String cacheKey = cacheKeyGenerator(NOTIFICATION_ENABLED_IDS, USER_ID, userId.toString());

        // 캐시에서 가져오기
        if (redisService.hasKey(cacheKey)) {
            return redisService.getSetAsLongListExcludeDummy(cacheKey);
        }

        // 캐시 미스 : DB에서 가져오기
        return getNotificationEnabledIdsFromDB(userId, cacheKey);
    }

    public void save(Follow follow) {
        Long senderId = follow.getSender().getId();
        Long recipientId = follow.getRecipient().getId();

        // Follower Ids 관련 캐시 처리
        String followerIdsKey = cacheKeyGenerator(FOLLOWER_IDS, USER_ID, recipientId.toString());
        handleFollowerCache(followerIdsKey, recipientId, senderId);

        // Following Ids 관련 캐시 처리
        String followingIdsKey = cacheKeyGenerator(FOLLOWING_IDS, USER_ID, senderId.toString());
        handleFollowingCache(followingIdsKey, senderId, recipientId);
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

    public void deleteFollowBySenderIdAndRecipientId(Long senderId,
                                                     Long recipientId) {
        repository.deleteBySenderIdAndRecipientId(senderId, recipientId);

        handleDeleteFollowCache(senderId, recipientId);
    }

    /**
     * 데이터베이스에서 팔로워 ID 목록을 조회하고 Redis 캐시에 저장합니다.
     *
     * @param userId    사용자 ID
     * @param cacheKey  캐시 키
     * @return 해당 사용자의 팔로워 ID 목록
     */
    private List<Long> getFollowerIdsFromDB(Long userId, String cacheKey) {
        List<Long> followerIds = repository.findByRecipientId(userId).stream()
                .map(Follow::getSender)
                .map(User::getId)
                .collect(Collectors.toList());

        return redisService.cacheListToSetWithDummy(followerIds, cacheKey, ONE_DAY_TTL);
    }

    /**
     * 데이터베이스에서 팔로잉 ID 목록을 조회하고 Redis 캐시에 저장합니다.
     *
     * @param userId    사용자 ID
     * @param cacheKey  캐시 키
     * @return 해당 사용자의 팔로잉 ID 목록
     */
    private List<Long> getFollowingIdsFromDB(Long userId, String cacheKey) {
        List<Long> followingIds = repository.findBySenderId(userId).stream()
                .map(Follow::getRecipient)
                .map(User::getId)
                .collect(Collectors.toList());

        return redisService.cacheListToSetWithDummy(followingIds, cacheKey, ONE_DAY_TTL);
    }

    /**
     * 데이터베이스에서 알림이 활성화된 사용자 ID 목록을 조회하고 Redis 캐시에 저장합니다.
     *
     * @param userId    사용자 ID
     * @param cacheKey  캐시 키
     * @return 알림이 활성화된 사용자 ID 목록
     */
    private List<Long> getNotificationEnabledIdsFromDB(Long userId, String cacheKey) {
        List<Long> notificationEnabledUserIds = repository.findByRecipientId(userId).stream()
                .filter(Follow::getIsNotificationEnabled)
                .map(Follow::getSender)
                .map(User::getId)
                .collect(Collectors.toList());

        return redisService.cacheListToSetWithDummy(notificationEnabledUserIds, cacheKey, ONE_DAY_TTL);
    }

    /**
     * 팔로잉 관련 캐시를 업데이트합니다.
     *
     * @param followingIds 캐시 키
     * @param senderId     팔로우 요청자 ID
     * @param recipientId  팔로우 대상자 ID
     */
    private void handleFollowingCache(String followingIds,
                                      Long senderId,
                                      Long recipientId) {
        // Write-back Sorted Set에 추가
        redisService.pushToWriteBackSortedSet(followingIds);

        // 캐시가 존재하지 않으면 DB에서 가져온다
        if (!redisService.hasKey(followingIds)) {
            getFollowingIdsFromDB(senderId, followingIds);
        }

        // 캐시에 following ID 추가
        redisService.addToSet(followingIds, recipientId, ONE_DAY_TTL);
    }

    /**
     * 팔로우 관계 삭제에 따른 캐시 업데이트를 수행합니다.
     *
     * @param senderId     팔로우 요청자 ID
     * @param recipientId  팔로우 대상자 ID
     */
    private void handleDeleteFollowCache(Long senderId,
                                         Long recipientId) {
        String followerIdsKey =
                cacheKeyGenerator(FOLLOWER_IDS, USER_ID, recipientId.toString());
        if (redisService.hasKey(followerIdsKey)) {
            redisService.removeFromSet(followerIdsKey, senderId);
        }

        String followingIdsKey =
                cacheKeyGenerator(FOLLOWING_IDS, USER_ID, senderId.toString());
        if (redisService.hasKey(followingIdsKey)) {
            redisService.removeFromSet(followingIdsKey, recipientId);
        }

        String notificationEnabledIdsKey =
                cacheKeyGenerator(NOTIFICATION_ENABLED_IDS, USER_ID, recipientId.toString());
        if (redisService.hasKey(notificationEnabledIdsKey)) {
            redisService.removeFromSet(notificationEnabledIdsKey, senderId);
        }
    }

    /**
     * 팔로워 관련 캐시를 업데이트합니다.
     *
     * @param followerIdsKey  캐시 키
     * @param recipientId  팔로우 대상자 ID
     * @param senderId     팔로우 요청자 ID
     */
    private void handleFollowerCache(String followerIdsKey,
                                     Long recipientId,
                                     Long senderId) {
        // Write-back Sorted Set에 추가
        redisService.pushToWriteBackSortedSet(followerIdsKey);

        // 캐시가 존재하지 않으면 DB에서 가져온다
        if (!redisService.hasKey(followerIdsKey)) {
            getFollowerIdsFromDB(recipientId, followerIdsKey);
        }

        // 캐시에 follower ID 추가
        redisService.addToSet(followerIdsKey, senderId, ONE_DAY_TTL);
    }
}
