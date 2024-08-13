package com.pigeon_stargram.sns_clone.service.notification;


import com.pigeon_stargram.sns_clone.domain.notification.Notification;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationConvertable;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.notification.response.ResponseNotificationDto;
import com.pigeon_stargram.sns_clone.repository.notification.NotificationRepository;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import com.pigeon_stargram.sns_clone.worker.NotificationWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class NotificationService {

    private final UserService userService;
    private final NotificationRepository notificationRepository;
    private final NotificationWorker notificationWorker;

    public List<Notification> save(NotificationConvertable dto) {
        User sender = userService.findById(dto.getSenderId());
        List<Notification> notifications = dto.getRecipientIds().stream()
                .map(userService::findById)
                .map(recipient -> dto.toNotification(sender, recipient))
                .toList();
        notifications.forEach(notificationWorker::enqueue);
        return notificationRepository.saveAll(notifications);
    }

    public List<Notification> findByUserId(Long userId) {
        User recipient = userService.findById(userId);
        return notificationRepository.findAllByRecipient(recipient);
    }

    public ResponseNotificationDto readNotification(Long id) {
        Notification notification = notificationRepository.findById(id).get();
        notification.setRead(true);
        return toResponseDto(notification);
    }

    public List<ResponseNotificationDto> readNotifications(User user) {
        List<Notification> notifications = notificationRepository.findAllByRecipient(user);
        notifications.forEach(notification -> {
            notification.setRead(true);
        });
        return notifications.stream()
                .map(this::toResponseDto)
                .toList();
    }

    public ResponseNotificationDto toResponseDto(Notification notification) {
        return ResponseNotificationDto.builder()
                .name(notification.getSender().getName())
                .content(notification.getMessage())
                .createdTime(notification.getCreatedDate())
                .isRead(notification.getIsRead())
                .build();
    }

}
