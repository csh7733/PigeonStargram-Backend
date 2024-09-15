package com.pigeon_stargram.sns_clone.service.notification;


import com.pigeon_stargram.sns_clone.domain.notification.NotificationContent;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationConvertable;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationV2;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.notification.internal.NotificationSplitDto;
import com.pigeon_stargram.sns_clone.dto.notification.response.ResponseNotificationDto;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import com.pigeon_stargram.sns_clone.worker.NotificationSplitWorker;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.dto.notification.NotificationDtoConvertor.buildResponseNotificationDto;

/**
 * NotificationService는 알림과 관련된 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * 알림 전송, 조회, 읽음 처리, 삭제 등의 기능을 제공합니다.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceV2 implements NotificationService{

    private final UserService userService;
    private final NotificationCrudService notificationCrudService;

    private final NotificationSplitWorker notificationSplitWorker;

    public void sendToSplitWorker(NotificationConvertable dto) {

        // 알림 콘텐츠 객체를 생성하고 저장
        NotificationContent content = dto.toNotificationContent();
        NotificationContent saveContent = notificationCrudService.saveContent(content);

        // Content 저장 후 커밋 전에 다른 worker에서 읽기를 시도할 때 Uncommitted read를 방지하기 위해 사용.
        // 현재 트랜잭션이 종료된 후 큐에 넣는다.
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                // 트랜잭션 커밋 후, 알림 분할 작업을 큐에 추가
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
                    // 알림 내용과 발신자 정보를 조회하여 DTO로 변환
                    NotificationContent content =
                            notificationCrudService.findContentById(notification.getContent().getId());
                    User sender = userService.getUserById(content.getSenderId());

                    return buildResponseNotificationDto(notification, sender, content);
                })
                .collect(Collectors.toList());
    }

    public void readNotification(Long notificationId) {
        NotificationV2 notification = notificationCrudService.findById(notificationId);
        notification.setRead(true); // 읽음 상태로 설정
    }

    public void readNotifications(Long userId) {
        List<NotificationV2> notifications =
                notificationCrudService.findNotificationByRecipientId(userId);

        notifications.forEach(notification -> notification.setRead(true)); // 모든 알림 읽음 처리
    }

    public void deleteNotification(Long notificationId) {

        notificationCrudService.deleteNotificationById(notificationId);
    }

    public void deleteAll(Long recipientId) {

        notificationCrudService.deleteAllNotificationByRecipientId(recipientId);
    }

    public void notifyTaggedUsers(NotificationConvertable dto) {

        if (!dto.toRecipientIds().isEmpty()) sendToSplitWorker(dto); // 태그된 유저가 있으면 워커에 알림 작업을 넘김
    }
}
