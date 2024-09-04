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
public class NotifyPostTaggedDto implements NotificationConvertable {

    private Long userId;
    private String userName;
    private String content;
    private List<Long> notificationRecipientIds;

    @Override
    public Notification toNotification(User sender,
                                       User recipient) {
        return Notification.builder()
                .recipient(recipient)
                .sender(sender)
                .type(NotificationType.POST_TAG)
                .isRead(false)
                .message(generateMessage())
                .sourceId(userId)
                .build();
    }

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
                .sourceId(userId)
                .build();
    }

    @Override
    public Long getSenderId() {
        return userId;
    }

    @Override
    public List<Long> getRecipientIds() {
        return notificationRecipientIds;
    }

    @Override
    public String generateMessage() {
        return userName + "님이 새 글에서 당신을 언급했습니다. 지금 " +
                userName +"님의 프로필로 가서 확인하세요!";
    }

}
