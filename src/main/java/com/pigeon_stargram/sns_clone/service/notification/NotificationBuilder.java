package com.pigeon_stargram.sns_clone.service.notification;

import com.pigeon_stargram.sns_clone.domain.notification.Notification;
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
}
