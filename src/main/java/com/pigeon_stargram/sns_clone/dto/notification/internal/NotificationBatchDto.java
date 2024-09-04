package com.pigeon_stargram.sns_clone.dto.notification.internal;

import com.pigeon_stargram.sns_clone.domain.notification.NotificationType;
import com.pigeon_stargram.sns_clone.domain.user.User;
import lombok.*;

import java.util.List;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationBatchDto {

    private Long senderId;
    private List<Long> batchRecipientIds;

    private NotificationType type;
    private String message;
    private Boolean isRead;
    private Long sourceId;
    private Long sourceId2;
}
