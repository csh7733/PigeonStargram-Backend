package com.pigeon_stargram.sns_clone.dto.notification.internal;

import com.pigeon_stargram.sns_clone.domain.notification.NotificationConvertable;
import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSplitDto {

    private NotificationConvertable notificationDto;
    private Long contentId;
}
