package com.pigeon_stargram.sns_clone.dto.notification.internal;

import com.pigeon_stargram.sns_clone.domain.notification.Notification;
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
    public Notification toNotification(User sender, User recipient) {
        return Notification.builder()
                .recipient(recipient)
                .sender(sender)
                .type(NotificationType.REPLY_TAG)
                .isRead(false)
                .message(generateMessage(sender, recipient))
                .sourceId(postUserId)
                .sourceId2(postId)
                .build();
    }

    @Override
    public NotificationBatchDto toNotificationBatchDto(User sender,
                                                       List<User> batchRecipients) {
        User recipient = batchRecipients.getFirst();

        return NotificationBatchDto.builder()
                .batchRecipients(batchRecipients)
                .sender(sender)
                .type(NotificationType.REPLY_TAG)
                .isRead(false)
                .message(generateMessage(sender, recipient))
                .sourceId(postUserId)
                .sourceId2(postId)
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
    public String generateMessage(User sender, User recipient) {
        return userName + "님이 답글에서 당신을 언급했습니다.";
    }

    @Override
    public String generateRedirectUrl(User sender, User recipient) {
        return "";
    }
}
