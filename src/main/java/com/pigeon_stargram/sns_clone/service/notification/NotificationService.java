package com.pigeon_stargram.sns_clone.service.notification;


import com.pigeon_stargram.sns_clone.domain.notification.Notification;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationConvertable;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.*;
import static com.pigeon_stargram.sns_clone.worker.WorkerConstants.*;

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
    public void send(NotificationConvertable dto) {
        Long senderId = dto.getSenderId();

        List<Long> recipientIds = dto.getRecipientIds();
        int iterationMax = getIterationMax(recipientIds);
        for (int i = 0; i < iterationMax; i++) {

            int leftIndex = i * BATCH_SIZE;
            int rightIndex = (i == iterationMax - 1) ?
                    recipientIds.size() : (i + 1) * BATCH_SIZE;
            log.info("left={}, right={}", leftIndex, rightIndex);

            List<Long> batchRecipientIds = getBatchRecipientIds(recipientIds, leftIndex, rightIndex);

            NotificationBatchDto notificationBatchDto =
                    dto.toNotificationBatchDto(senderId, batchRecipientIds);

            insertIntoMessageQueue(notificationBatchDto);
        }
    }

    private List<Long> getBatchRecipientIds(List<Long> recipientIds,
                                            int leftIndex,
                                            int rightIndex) {
        List<Long> subList = recipientIds.subList(leftIndex, rightIndex);
        List<Long> batchIds = new ArrayList<>(subList);
        return batchIds;
    }

    private static int getIterationMax(List<Long> recipientIds) {
        return (recipientIds.size() - 1) / BATCH_SIZE + 1;
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
        return notificationRepository.findAllByRecipientId(userId).stream()
                .filter(notification -> !notification.getIsRead())
                .map(NotificationBuilder::buildResponseNotificationDto)
                .collect(Collectors.toList());
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

        if(!dto.getRecipientIds().isEmpty()) send(dto);
    }
}
