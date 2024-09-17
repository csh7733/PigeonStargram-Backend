package com.pigeon_stargram.sns_clone.service.notification;

import com.pigeon_stargram.sns_clone.domain.notification.NotificationContent;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationV2;
import com.pigeon_stargram.sns_clone.exception.notification.NotificationNotFoundException;
import com.pigeon_stargram.sns_clone.repository.notification.NotificationContentRepository;
import com.pigeon_stargram.sns_clone.repository.notification.NotificationV2Repository;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.*;
import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.*;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.*;

/**
 * 알림 정보에 대한 캐싱을 적용한 NotificationCrudService 구현체
 *
 * Value       | Structure | Key
 * ----------- | --------- | ------------------------------------------
 * contentIds  | Set       | NOTIFICATION_CONTENT_IDS_USER_ID_{userId}  (특정 사용자에 대한 모든 알림 contentId)
 * content     | String    | NOTIFICATION_CONTENT_ID_{contentId}        (특정 알림 content 정보 캐싱)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationCrudServiceV2 implements NotificationCrudService{

    private final RedisService redisService;

    private final NotificationV2Repository notificationRepository;
    private final NotificationContentRepository contentRepository;
    private final NotificationContentRepository notificationContentRepository;

    @Transactional(readOnly = true)
    public List<NotificationV2> findNotificationByRecipientId(Long recipientId) {

        return notificationRepository.findByRecipientId(recipientId);
    }

    @Transactional(readOnly = true)
    public NotificationV2 findById(Long notificationId) {

        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException(NOTIFICATION_NOT_FOUND_ID));
    }

    @Cacheable(value = NOTIFICATION_CONTENT,
            key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).NOTIFICATION_CONTENT_ID + '_' + #contentId")
    @Transactional(readOnly = true)
    public NotificationContent findContentById(Long contentId) {

        return notificationContentRepository.findById(contentId)
                .orElseThrow(() -> new NotificationNotFoundException(NOTIFICATION_CONTENT_NOT_FOUND_ID));
    }

    @Transactional
    public List<NotificationV2> saveAll(List<NotificationV2> notifications) {
        // 알림 전체 저장
        List<NotificationV2> savedNotifications = notificationRepository.saveAll(notifications);

        savedNotifications.forEach(notification -> {
            String contentIdsKey =
                    cacheKeyGenerator(NOTIFICATION_CONTENT_IDS, USER_ID, notification.getRecipientId().toString());

            // 캐시가 존재할 경우 캐시에 저장된 contentId 세트에 새로운 contentId 추가
            if (redisService.hasKey(contentIdsKey)) {
                addContentIdToCache(contentIdsKey, notification.getContent().getId());
            }
        });

        return savedNotifications;
    }

    @CachePut(value = NOTIFICATION_CONTENT,
            key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).NOTIFICATION_CONTENT_ID + '_' + #content.id")
    @Transactional
    public NotificationContent saveContent(NotificationContent content) {

        return contentRepository.save(content);
    }

    @Transactional
    public void deleteNotificationById(Long notificationId) {

        notificationRepository.deleteById(notificationId);
    }

    @Transactional
    public void deleteAllNotificationByRecipientId(Long recipientId) {

        notificationRepository.deleteAllByRecipientId(recipientId);
    }

    private void addContentIdToCache(String contentIdsKey, Long contentId) {

        redisService.addToSet(contentIdsKey, contentId, ONE_DAY_TTL);
    }

}
