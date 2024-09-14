package com.pigeon_stargram.sns_clone.dto.Follow.internal;

import com.pigeon_stargram.sns_clone.domain.follow.Follow;
import com.pigeon_stargram.sns_clone.domain.notification.Notification;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationContent;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationConvertable;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationType;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.notification.internal.NotificationBatchDto;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

import static com.pigeon_stargram.sns_clone.domain.notification.NotificationType.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AddFollowDto implements NotificationConvertable {

    private Long senderId;
    private String senderName;
    private Long recipientId;

    @Override
    public NotificationBatchDto toNotificationBatchDto(Long senderId,
                                                       List<Long> batchRecipientIds,
                                                       Long contentId) {
        return NotificationBatchDto.builder()
                .senderId(senderId)
                .batchRecipientIds(batchRecipientIds)
                .contentId(contentId)
                .build();
    }

    @Override
    public NotificationContent toNotificationContent() {
        return NotificationContent.builder()
                .senderId(senderId)
                .type(FOLLOW)
                .message(generateMessage())
                .sourceId(senderId)
                .build();
    }

    @Override
    public List<Long> toRecipientIds() {
        return Arrays.asList(recipientId);
    }

    @Override
    public String generateMessage() {
        return senderName + "님이 나를 팔로우 했습니다.";
    }

}
