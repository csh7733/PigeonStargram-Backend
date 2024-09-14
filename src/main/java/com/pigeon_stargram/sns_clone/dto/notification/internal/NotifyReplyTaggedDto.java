package com.pigeon_stargram.sns_clone.dto.notification.internal;

import com.pigeon_stargram.sns_clone.domain.notification.Notification;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationContent;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationConvertable;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationType;
import com.pigeon_stargram.sns_clone.domain.user.User;
import lombok.*;

import java.util.List;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotifyReplyTaggedDto implements NotificationConvertable {

    private Long userId;
    private String userName;
    private String content;
    private Long postUserId;
    private Long postId;
    private List<Long> notificationRecipientIds;

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
                .senderId(userId)
                .type(NotificationType.POST_TAG)
                .message(generateMessage())
                .sourceId(postUserId)
                .sourceId2(postId)
                .build();
    }

    @Override
    public Long getSenderId() {
        return userId;
    }

    @Override
    public List<Long> toRecipientIds() {
        return notificationRecipientIds;
    }

    @Override
    public String generateMessage() {
        return userName + "님이 답글에서 당신을 언급했습니다.";
    }

}
