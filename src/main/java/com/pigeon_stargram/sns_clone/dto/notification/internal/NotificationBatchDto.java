package com.pigeon_stargram.sns_clone.dto.notification.internal;

import com.pigeon_stargram.sns_clone.domain.notification.NotificationContent;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationType;
import com.pigeon_stargram.sns_clone.domain.user.User;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NotificationBatchDto {

    private Long senderId;
    private List<Long> batchRecipientIds;
    private Long contentId;
}
