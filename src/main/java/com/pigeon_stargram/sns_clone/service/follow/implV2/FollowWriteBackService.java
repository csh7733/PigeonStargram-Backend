package com.pigeon_stargram.sns_clone.service.follow.implV2;

import com.pigeon_stargram.sns_clone.domain.follow.Follow;
import com.pigeon_stargram.sns_clone.domain.follow.FollowFactory;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.repository.follow.FollowRepository;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import com.pigeon_stargram.sns_clone.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.NOTIFICATION_ENABLED_IDS;
import static com.pigeon_stargram.sns_clone.constant.CacheConstants.USER_ID;
import static com.pigeon_stargram.sns_clone.domain.follow.FollowFactory.*;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.cacheKeyGenerator;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class FollowWriteBackService {

    private final RedisService redisService;
    private final UserService userService;

    private final FollowRepository followRepository;

    public void syncFollowingIds(String key) {
        Long senderId = RedisUtil.parseSuffix(key);
        log.info("WriteBack key={}", key);

        List<Long> followingIds = redisService.getSetAsLongListExcludeDummy(key);

        for (Long recipientId : followingIds) {
            // 중복 여부 확인
            if (!followRepository.existsBySenderIdAndRecipientId(senderId, recipientId)) {
                Follow follow = getFollow(senderId, recipientId);
                followRepository.save(follow);
            }
        }
    }

    public void syncFollowerIds(String key) {
        Long recipientId = RedisUtil.parseSuffix(key);
        log.info("WriteBack key={}", key);

        List<Long> followerIds = redisService.getSetAsLongListExcludeDummy(key);

        for (Long senderId : followerIds) {
            // 중복 여부 확인
            if (!followRepository.existsBySenderIdAndRecipientId(senderId, recipientId)) {
                Follow follow = getFollow(senderId, recipientId);
                followRepository.save(follow);
            }
        }
    }

    public void syncNotificationEnabledIds(String key) {
        Long recipientId = RedisUtil.parseSuffix(key);  // recipientId 추출
        log.info("WriteBack key={}", key);

        // Recipient에 대한 Follow 목록을 DB에서 가져오기
        List<Follow> followsFromDB = followRepository.findByRecipientId(recipientId);

        // 캐시에서 알림 신청한 userIds 가져오기
        List<Long> enableFollowingIds = redisService.getSetAsLongListExcludeDummy(key);

        // 캐시와 DB 간 차이점 확인
        for (Follow follow : followsFromDB) {
            Long senderId = follow.getSender().getId();

            // 캐시에 senderId가 있지만, DB에서 알림이 비활성화된 경우 -> 알림 활성화
            if (enableFollowingIds.contains(senderId) && !follow.getIsNotificationEnabled()) {
                // DB 알림 비활성화 -> 활성화
                follow.toggleNotificationEnabled();
                followRepository.save(follow);
            }
            // 캐시에 senderId가 없고, DB에서 알림이 활성화된 경우 -> 알림 비활성화
            else if (!enableFollowingIds.contains(senderId) && follow.getIsNotificationEnabled()) {
                // DB 알림 활성화 -> 비활성화
                follow.toggleNotificationEnabled();
                followRepository.save(follow);
            }
        }

    }

    private Boolean getisEnabled(Long senderId,
                                 Long recipientId) {
        String cacheKey = cacheKeyGenerator(NOTIFICATION_ENABLED_IDS, USER_ID, recipientId.toString());

        return redisService.hasKey(cacheKey) && redisService.isMemberOfSet(cacheKey, senderId);
    }

    private Follow getFollow(Long senderId,
                             Long recipientId) {
        User sender = userService.getUserById(senderId);
        User recipient = userService.getUserById(recipientId);
        Boolean isEnabled = getisEnabled(senderId, recipientId);

        return createFollow(sender, recipient, isEnabled);
    }

}
