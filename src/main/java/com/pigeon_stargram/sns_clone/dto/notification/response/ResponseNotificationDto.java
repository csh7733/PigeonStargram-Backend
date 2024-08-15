package com.pigeon_stargram.sns_clone.dto.notification.response;

import com.pigeon_stargram.sns_clone.domain.notification.Notification;
import lombok.*;

import java.time.LocalDateTime;

import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.formatTime;
import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.getCurrentFormattedTime;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResponseNotificationDto {

    private String name;
    private String content;
    private Boolean isRead;
    private String time;
    private Long targetUserId;

    public ResponseNotificationDto (Notification notification) {
        this.name = notification.getSender().getName();
        this.content = notification.getMessage();
        this.isRead = notification.getIsRead();
        this.time = getCurrentFormattedTime();
        this.targetUserId = notification.getRecipient().getId();
    }
}
