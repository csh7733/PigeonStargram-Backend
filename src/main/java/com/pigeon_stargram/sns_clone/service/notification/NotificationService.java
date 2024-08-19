package com.pigeon_stargram.sns_clone.service.notification;


import com.pigeon_stargram.sns_clone.domain.notification.Notification;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationConvertable;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.notification.response.ResponseNotificationDto;
import com.pigeon_stargram.sns_clone.exception.notification.NotificationNotFoundException;
import com.pigeon_stargram.sns_clone.repository.notification.NotificationRepository;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import com.pigeon_stargram.sns_clone.worker.NotificationWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.*;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class NotificationService {

    private final UserService userService;
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

        List<Notification> saveNotifications = notificationRepository.saveAll(notifications);

        notifications.stream()
                .map(ResponseNotificationDto::new)
                .forEach(notificationWorker::enqueue);

        return saveNotifications;
    }

    public List<ResponseNotificationDto> findUnreadNotifications(Long userId) {
        return notificationRepository.findAllByRecipientId(userId).stream()
                .filter(notification -> !notification.getIsRead())
                .map(ResponseNotificationDto::new)
                .toList();
    }

    public void readNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException(NOTIFICATION_NOT_FOUND_ID));
        notification.setRead(true);
    }

    public void readNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findAllByRecipientId(userId);
        notifications.forEach(notification -> {
            notification.setRead(true);
        });
    }

    public void notifyTaggedUsers(NotificationConvertable dto) {
        if(!dto.getRecipientIds().isEmpty()) save(dto);
    }
}
