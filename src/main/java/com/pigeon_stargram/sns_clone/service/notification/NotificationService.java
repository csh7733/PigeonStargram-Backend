package com.pigeon_stargram.sns_clone.service.notification;


import com.pigeon_stargram.sns_clone.domain.notification.Notification;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationConvertable;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.notification.response.ResponseNotificationDto;
import com.pigeon_stargram.sns_clone.repository.notification.NotificationRepository;
import com.pigeon_stargram.sns_clone.service.user.BasicUserService;
import com.pigeon_stargram.sns_clone.worker.NotificationWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class NotificationService {

    private final BasicUserService userService;
    private final NotificationRepository notificationRepository;
    private final NotificationWorker notificationWorker;

    /**
     * TODO : ID생성방식 변화시키기 (지연쓰기를 위해서)
     */
    public List<Notification> save(NotificationConvertable dto) {
        Long senderId = dto.getSenderId();
        User sender = userService.findById(senderId);

        List<Notification> notifications = dto.getRecipientIds().stream()
                .filter(recipientId -> !recipientId.equals(senderId))
                .map(userService::findById)
                .map(recipient -> dto.toNotification(sender, recipient))
                .toList();

        List<Notification> save = notificationRepository.saveAll(notifications);

        notifications.stream()
                .map(ResponseNotificationDto::new)
                .forEach(notificationWorker::enqueue);

        return save;
    }

    public List<ResponseNotificationDto> findUnReadNotifications(Long userId) {
        User recipient = userService.findById(userId);
        return notificationRepository.findAllByRecipient(recipient).stream()
                .filter(notification -> !notification.getIsRead())
                .map(ResponseNotificationDto::new)
                .toList();
    }

    public void readNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).get();
        notification.setRead(true);
    }

    public void readNotifications(Long userId) {
        User user = userService.findById(userId);
        List<Notification> notifications = notificationRepository.findAllByRecipient(user);
        notifications.forEach(notification -> {
            notification.setRead(true);
        });
    }

}
