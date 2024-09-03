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

    // Notification의 형식과 최대한 비슷하게 하기 위해 userId 대신 User을 사용
    private User sender;
    private List<User> batchRecipients;

    private NotificationType type;
    private String message;
    private Boolean isRead;
    private Long sourceId;
    private Long sourceId2;
}
