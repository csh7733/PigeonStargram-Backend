package com.pigeon_stargram.sns_clone.service.notification;


import com.pigeon_stargram.sns_clone.domain.notification.Notification;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationContent;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationConvertable;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationV2;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.notification.internal.NotificationSplitDto;
import com.pigeon_stargram.sns_clone.dto.notification.response.ResponseNotificationDto;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import com.pigeon_stargram.sns_clone.worker.NotificationSplitWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.service.notification.NotificationBuilder.buildResponseNotificationDto;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class NotificationService {

    private final UserService userService;
    private final NotificationCrudService notificationCrudService;

    private final NotificationSplitWorker notificationSplitWorker;

    public void sendToSplitWorker(NotificationConvertable dto) {

        NotificationContent content = dto.toNotificationContent();
        NotificationContent saveContent = notificationCrudService.saveContent(content);

        // Content 저장 후 커밋 전에 다른 worker에서 읽기를 시도할 때 Uncommitted read를 방지하기 위해 사용.
        // 현재 트랜잭션이 종료된 후 큐에 넣는다.
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                NotificationSplitDto task = NotificationSplitDto.builder()
                        .notificationDto(dto)
                        .contentId(saveContent.getId())
                        .build();
                notificationSplitWorker.enqueue(task);
            }
        });
    }

    public List<ResponseNotificationDto> findByUserId(Long userId) {
        List<NotificationV2> notifications = notificationCrudService.findNotificationByRecipientId(userId);

        return notifications.stream()
                .map(notification -> {
                    NotificationContent content =
                            notificationCrudService.findContentById(notification.getContent().getId());
                    User sender = userService.getUserById(content.getSenderId());

                    return buildResponseNotificationDto(notification, sender, content);
                })
                .collect(Collectors.toList());
    }

    public void readNotification(Long notificationId) {
        NotificationV2 notification = notificationCrudService.findById(notificationId);
        notification.setRead(true);
    }

    public void readNotifications(Long userId) {
        List<NotificationV2> notifications =
                notificationCrudService.findNotificationByRecipientId(userId);

        notifications.forEach(notification -> notification.setRead(true));
    }

    public void deleteNotification(Long notificationId) {

        notificationCrudService.deleteNotificationById(notificationId);
    }

    public void deleteAll(Long recipientId) {

        notificationCrudService.deleteAllNotificationByRecipientId(recipientId);
    }

    public void notifyTaggedUsers(NotificationConvertable dto) {

        if (!dto.toRecipientIds().isEmpty()) sendToSplitWorker(dto);
    }
}
