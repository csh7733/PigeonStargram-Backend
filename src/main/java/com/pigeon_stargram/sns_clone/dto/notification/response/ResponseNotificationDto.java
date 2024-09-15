package com.pigeon_stargram.sns_clone.dto.notification.response;

import com.pigeon_stargram.sns_clone.domain.notification.NotificationType;
import lombok.*;

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
}
