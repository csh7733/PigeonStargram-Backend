package com.pigeon_stargram.sns_clone.dto.notification.response;

import com.pigeon_stargram.sns_clone.domain.notification.Notification;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationType;
import lombok.*;

import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.formatTime;
import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.getCurrentFormattedTime;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResponseWebSocketNotificationDto {

    private Long id;
    private String name;
    private String avatar;
    private String content;
    private Boolean isRead;
    private String time;
    private Long targetUserId;
    private Long noticeSourceId;
    private NotificationType type;

    public ResponseWebSocketNotificationDto(Notification notification) {
        this.id = notification.getId();
        this.type = notification.getType();
        this.name = notification.getSender().getName();
        this.avatar = notification.getSender().getAvatar();
        this.content = notification.getMessage();
        this.isRead = notification.getIsRead();
        this.time = getCurrentFormattedTime();
        this.targetUserId = notification.getRecipient().getId();
        this.noticeSourceId =  notification.getNoticeSourceId();
    }
}
