package com.pigeon_stargram.sns_clone.service.notification;


import com.pigeon_stargram.sns_clone.domain.notification.Notification;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationContent;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationConvertable;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationV2;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.notification.internal.NotificationBatchDto;
import com.pigeon_stargram.sns_clone.dto.notification.response.ResponseNotificationDto;
import com.pigeon_stargram.sns_clone.exception.notification.NotificationNotFoundException;
import com.pigeon_stargram.sns_clone.repository.notification.NotificationRepository;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import com.pigeon_stargram.sns_clone.worker.NotificationWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.constant.WorkerConstants.BATCH_SIZE;
import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.NOTIFICATION_NOT_FOUND_ID;
import static com.pigeon_stargram.sns_clone.service.notification.NotificationBuilder.buildResponseNotificationDto;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class NotificationService {

    private final UserService userService;
    private final NotificationCrudService notificationCrudService;

    private final NotificationRepository notificationRepository;

    private final NotificationWorker notificationWorker;

    private static int getIterationMax(List<Long> recipientIds) {
        return (recipientIds.size() - 1) / BATCH_SIZE + 1;
    }

    /**
     * TODO : ID생성방식 변화시키기 (지연쓰기를 위해서)
     */
    public void send(NotificationConvertable dto) {
        Long senderId = dto.getSenderId();

        NotificationContent content = dto.toNotificationContent();
        NotificationContent saveContent = notificationCrudService.saveContent(content);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                List<Long> recipientIds = dto.getRecipientIds();
                int iterationMax = getIterationMax(recipientIds);
                for (int i = 0; i < iterationMax; i++) {

                    int leftIndex = i * BATCH_SIZE;
                    int rightIndex = (i == iterationMax - 1) ?
                            recipientIds.size() : (i + 1) * BATCH_SIZE;
                    log.info("left={}, right={}", leftIndex, rightIndex);

                    List<Long> batchRecipientIds = getBatchRecipientIds(recipientIds, leftIndex, rightIndex).stream()
                            .filter(recipientId -> !senderId.equals(recipientId))
                            .collect(Collectors.toList());

                    NotificationBatchDto notificationBatchDto =
                            dto.toNotificationBatchDto(senderId, batchRecipientIds, saveContent.getId());

                    insertIntoMessageQueue(notificationBatchDto);
                }
            }
        });
    }

    private List<Long> getBatchRecipientIds(List<Long> recipientIds,
                                            int leftIndex,
                                            int rightIndex) {
        List<Long> subList = recipientIds.subList(leftIndex, rightIndex);
        List<Long> batchIds = new ArrayList<>(subList);
        return batchIds;
    }

    private void insertIntoMessageQueue(List<Notification> notifications) {
        notifications.stream()
                .map(NotificationBuilder::buildResponseNotificationDto)
                .forEach(notificationWorker::enqueue);
    }

    private void insertIntoMessageQueue(NotificationBatchDto dto) {
        notificationWorker.enqueue(dto);
    }

    private List<Notification> convertDtoToNotifications(NotificationConvertable dto, Long senderId, User sender) {
        return dto.getRecipientIds().stream()
                .filter(recipientId -> !recipientId.equals(senderId))
                .map(userService::findById)
                .map(recipient -> dto.toNotification(sender, recipient))
                .collect(Collectors.toList());
    }

    public List<ResponseNotificationDto> findUnreadNotifications(Long userId) {
        List<NotificationV2> notifications = notificationCrudService.findNotificationByRecipientId(userId);

        return notifications.stream()
                .filter(notification -> !notification.getIsRead())
                .map(notification -> {
                    NotificationContent content =
                            notificationCrudService.findContentById(notification.getContent().getId());
                    User sender = userService.findById(content.getSenderId());

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

    public void notifyTaggedUsers(NotificationConvertable dto) {

        if (!dto.getRecipientIds().isEmpty()) send(dto);
    }
}
