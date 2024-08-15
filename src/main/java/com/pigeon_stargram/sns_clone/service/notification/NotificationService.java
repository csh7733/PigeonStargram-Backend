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

    public List<Notification> save(NotificationConvertable dto) {
        User sender = userService.findById(dto.getSenderId());
        List<Notification> notifications = dto.getRecipientIds().stream()
                .map(userService::findById)
                .map(recipient -> dto.toNotification(sender, recipient))
                .toList();
        notifications.forEach(notificationWorker::enqueue);
        return notificationRepository.saveAll(notifications);
    }

    public List<ResponseNotificationDto> findByUserId(Long userId) {
        User recipient = userService.findById(userId);
        return notificationRepository.findAllByRecipient(recipient).stream()
                .map(this::toResponseDto)
                .toList();
    }

    public ResponseNotificationDto readNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).get();
        notification.setRead(true);
        return toResponseDto(notification);
    }

    public List<ResponseNotificationDto> readNotifications(Long userId) {
        User user = userService.findById(userId);
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
