package com.pigeon_stargram.sns_clone.service.notification;

import com.pigeon_stargram.sns_clone.domain.notification.NotificationV2;
import com.pigeon_stargram.sns_clone.repository.notification.NotificationContentRepository;
import com.pigeon_stargram.sns_clone.repository.notification.NotificationV2Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class NotificationCrudService {

    NotificationV2Repository notificationRepository;
    NotificationContentRepository contentRepository;

    public NotificationV2 save(NotificationV2 notification) {
        notificationRepository.save(notification);
    }

}
