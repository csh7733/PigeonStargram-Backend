package com.pigeon_stargram.sns_clone.dto.notification;

import com.pigeon_stargram.sns_clone.domain.notification.v1.NotificationV1;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationContent;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationV2;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.notification.response.ResponseNotificationDto;

import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.UNSUPPORTED_OPERATION;
import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.formatTime;

/**
 * NotificationDtoConvertor 클래스는 알림(Notification) 엔티티를 다양한 DTO로 변환하는 유틸리티 클래스입니다.
 */
public class NotificationDtoConvertor {

    private NotificationDtoConvertor() {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
    }

    /**
     * NotificationV1 객체를 ResponseNotificationDto로 변환하는 메서드입니다.
     *
     * @param notification NotificationV1 객체
     * @return ResponseNotificationDto 객체
     */
    public static ResponseNotificationDto buildResponseNotificationDto(NotificationV1 notification) {
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

    /**
     * NotificationV2 및 NotificationContent 객체를 사용해 ResponseNotificationDto로 변환하는 메서드입니다.
     *
     * @param notification NotificationV2 객체
     * @param sender       알림을 보낸 사용자
     * @param saveContent  알림 콘텐츠
     * @return ResponseNotificationDto 객체
     */
    public static ResponseNotificationDto buildResponseNotificationDto(NotificationV2 notification,
                                                                        User sender,
                                                                        NotificationContent saveContent) {
        return ResponseNotificationDto.builder()
                .id(notification.getId())
                .name(sender.getName())
                .avatar(sender.getAvatar())
                .content(saveContent.getMessage())
                .isRead(notification.getIsRead())
                .time(formatTime(notification.getCreatedDate()))
                .targetUserId(notification.getRecipientId())
                .type(saveContent.getType())
                .sourceId(saveContent.getSourceId())
                .sourceId2(saveContent.getSourceId2())
                .build();
    }

}
