package com.pigeon_stargram.sns_clone.service.notification;

import com.pigeon_stargram.sns_clone.domain.notification.NotificationContent;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationV2;
import com.pigeon_stargram.sns_clone.repository.notification.NotificationContentRepository;
import com.pigeon_stargram.sns_clone.repository.notification.NotificationV2Repository;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.*;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.*;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class NotificationCrudService {

    private final RedisService redisService;

    private final NotificationV2Repository notificationRepository;
    private final NotificationContentRepository contentRepository;

    public NotificationV2 save(NotificationV2 notification) {
        Long recipientId = notification.getRecipientId();
        NotificationV2 save = notificationRepository.save(notification);

        String contentIdsKey =
                cacheKeyGenerator(NOTIFICATION_CONTENT_IDS, USER_ID, recipientId.toString());
        if (redisService.hasKey(contentIdsKey)) {
            log.info("notification 저장후 recipientId에 대한 모든 contentId 캐시 저장 recipientId = {}", recipientId);
            redisService.addToSet(contentIdsKey, notification.getContent().getId());
        }

        return save;
    }

    @CachePut(value = NOTIFICATION_CONTENT,
            key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).NOTIFICATION_CONTENT_ID + '_' + #content.id")
    public NotificationContent saveContent(NotificationContent content) {
        return contentRepository.save(content);
    }

}
