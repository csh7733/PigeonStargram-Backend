package com.pigeon_stargram.sns_clone.service.follow;

import com.pigeon_stargram.sns_clone.domain.follow.Follow;
import com.pigeon_stargram.sns_clone.domain.post.Post;
import com.pigeon_stargram.sns_clone.domain.post.PostLike;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.repository.follow.FollowRepository;
import com.pigeon_stargram.sns_clone.repository.post.PostLikeRepository;
import com.pigeon_stargram.sns_clone.service.post.PostService;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import com.pigeon_stargram.sns_clone.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.NOTIFICATION_ENABLED_IDS;
import static com.pigeon_stargram.sns_clone.constant.CacheConstants.USER_ID;
import static com.pigeon_stargram.sns_clone.service.follow.FollowBuilder.buildFollow;
import static com.pigeon_stargram.sns_clone.service.post.PostBuilder.buildPostLike;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.cacheKeyGenerator;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class FollowWriteBackService {

    private final RedisService redisService;
    private final UserService userService;
    private final FollowRepository followRepository;
    public void syncFollowingIds(String key) {
        Long senderId = RedisUtil.parseSuffix(key);
        log.info("WriteBack key={}", key);

        // Sender 찾기
        User sender = userService.findById(senderId);

        // followingIds 리스트 가져오기 (Dummy 제외)
        List<Long> followingIds = redisService.getSetAsLongListExcludeDummy(key);

        for (Long recipientId : followingIds) {
            // 중복 여부 확인
            if (!followRepository.existsBySenderIdAndRecipientId(senderId, recipientId)) {
                User recipient = userService.findById(recipientId);
                log.info("sender = {},recipient = {}",senderId,recipientId);
                // Follow 객체 생성 및 저장
                // 알림 신청 여부에 따라 저장한다
                Boolean isEnabled = getisEnabled(senderId, recipientId);
                Follow follow = buildFollow(sender, recipient, isEnabled);
                followRepository.save(follow);
            }
        }
    }
    public void syncFollowerIds(String key) {
        Long recipientId = RedisUtil.parseSuffix(key);
        log.info("WriteBack key={}", key);

        // Recipient 찾기
        User recipient = userService.findById(recipientId);

        // followerIds 리스트 가져오기 (Dummy 제외)
        List<Long> followerIds = redisService.getSetAsLongListExcludeDummy(key);

        for (Long senderId : followerIds) {
            // 중복 여부 확인
            if (!followRepository.existsBySenderIdAndRecipientId(senderId, recipientId)) {
                User sender = userService.findById(senderId);
                // Follow 객체 생성 및 저장
                // 알림 신청 여부에 따라 저장한다
                Boolean isEnabled = getisEnabled(senderId, recipientId);
                Follow follow = buildFollow(sender, recipient, isEnabled);
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

    private Boolean getisEnabled(Long senderId, Long recipientId) {
        String cacheKey = cacheKeyGenerator(NOTIFICATION_ENABLED_IDS, USER_ID, recipientId.toString());
        Boolean isEnabled = redisService.hasKey(cacheKey) && redisService.isMemberOfSet(cacheKey, senderId);
        return isEnabled;
    }


}
