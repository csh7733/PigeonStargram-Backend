package com.pigeon_stargram.sns_clone.dto.notification.response;

import com.pigeon_stargram.sns_clone.domain.notification.Notification;
import lombok.*;

import java.time.LocalDateTime;

import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.formatTime;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseNotificationDto {

    private String name;
    private String content;
    private Boolean isRead;
    private String time;

    public ResponseNotificationDto (Notification notification) {
        this.name = notification.getSender().getName();
        this.content = notification.getMessage();
        this.isRead = notification.getIsRead();
        this.time = formatTime(notification.getCreatedDate());
    }
}
