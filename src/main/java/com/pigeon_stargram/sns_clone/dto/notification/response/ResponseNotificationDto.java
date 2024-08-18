package com.pigeon_stargram.sns_clone.dto.notification.response;

import com.pigeon_stargram.sns_clone.domain.notification.Notification;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationType;
import lombok.*;

import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.formatTime;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResponseNotificationDto {

    private Long id;
    private String name;
    private String avatar;
    private String content;
    private Boolean isRead;
    private String time;
    private Long targetUserId;
    private NotificationType type;
    private Long sourceId;

    // Type이 일경우 필요한 필드
    // MY_POST_COMMENT ||
    // MY_POST_LIKE ||
    // MY_COMMENT_REPLY ||
    // MY_COMMENT_LIKE ||
    // MY_REPLY_LIKE

    private Long sourceId2;



    public ResponseNotificationDto (Notification notification) {
        this.id = notification.getId();
        this.type = notification.getType();
        this.name = notification.getSender().getName();
        this.avatar = notification.getSender().getAvatar();
        this.content = notification.getMessage();
        this.isRead = notification.getIsRead();
        this.time = formatTime(notification.getCreatedDate());
        this.targetUserId = notification.getRecipient().getId();
        this.sourceId =  notification.getSourceId();
        this.sourceId2 = notification.getSourceId2();
    }
}
