package com.pigeon_stargram.sns_clone.service.notification;

import com.pigeon_stargram.sns_clone.domain.notification.Notification;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationContent;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationV2;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.notification.internal.NotificationBatchDto;
import com.pigeon_stargram.sns_clone.dto.notification.response.ResponseNotificationDto;

import java.time.LocalDateTime;

import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.UNSUPPORTED_OPERATION;
import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.formatTime;

public class NotificationBuilder {

    private NotificationBuilder() {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
    }

    public static ResponseNotificationDto buildResponseNotificationDto(Notification notification) {
        return ResponseNotificationDto.builder()
                .id(notification.getId())
                .type(notification.getType())
                .name(notification.getSender().getName())
                .avatar(notification.getSender().getAvatar())
                .content(notification.getMessage())
                .isRead(notification.getIsRead())
                // 테스트를 위해 임시로 사용
//                .time(formatTime(LocalDateTime.now()))
                .time(formatTime(notification.getCreatedDate()))
                .targetUserId(notification.getRecipient().getId())
                .sourceId(notification.getSourceId())
                .sourceId2(notification.getSourceId2())
                .build();
    }

    public static ResponseNotificationDto buildResponseNotificationDto(NotificationV2 save,
                                                                        User sender,
                                                                        NotificationContent saveContent) {
        return ResponseNotificationDto.builder()
                .id(save.getId())
                .name(sender.getName())
                .avatar(sender.getAvatar())
                .content(saveContent.getMessage())
                .isRead(save.getIsRead())
                .time(formatTime(save.getCreatedDate()))
                .targetUserId(save.getRecipientId())
                .type(saveContent.getType())
                .sourceId(saveContent.getSourceId())
                .sourceId2(saveContent.getSourceId2())
                .build();
    }

    public static NotificationV2 buildNotification(Long recipientId,
                                                    NotificationContent content) {
        return NotificationV2.builder()
                .recipientId(recipientId)
                .content(content)
                .isRead(false)
                .build();
    }
}
