package com.pigeon_stargram.sns_clone.dto.Follow.internal;

import com.pigeon_stargram.sns_clone.domain.follow.Follow;
import com.pigeon_stargram.sns_clone.domain.notification.Notification;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationConvertable;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationType;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.notification.internal.NotificationBatchDto;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddFollowDto implements NotificationConvertable {
    private Long senderId;
    private Long recipientId;

    public Follow toEntity(User sender, User recipient){
        return Follow.builder()
                .sender(sender)
                .recipient(recipient)
                .isNotificationEnabled(false)
                .build();
    }

    @Override
    public Notification toNotification(User sender, User recipient){
        return Notification.builder()
                .sender(sender)
                .recipient(recipient)
                .isRead(false)
                .type(getNotificationType(sender, recipient))
                .message(generateMessage(sender, recipient))
                .sourceId(senderId)
                .build();
    }

    @Override
    public NotificationBatchDto toNotificationBatchDto(User sender,
                                                       List<User> batchRecipients) {
        User recipient = batchRecipients.getFirst();

        return NotificationBatchDto.builder()
                .sender(sender)
                .batchRecipients(batchRecipients)
                .isRead(false)
                .type(getNotificationType(sender, recipient))
                .message(generateMessage(sender, recipient))
                .sourceId(senderId)
                .build();
    }

    @Override
    public List<Long> getRecipientIds() {
        return Arrays.asList(recipientId);
    }

    public NotificationType getNotificationType(User sender, User recipient) {
        return isFollowBack(sender, recipient)
                ? NotificationType.FOLLOW_BACK
                : NotificationType.FOLLOW;
    }

    private boolean isFollowBack(User sender, User recipient) {
        return recipient.getFollowings().stream()
                .map(Follow::getRecipient)
                .toList()
                .contains(sender);
    }

    @Override
    public String generateMessage(User sender, User recipient) {
        return sender.getName() +
                (isFollowBack(sender, recipient)
                ? "님이 나를 맞팔로우 했습니다."
                : "님이 나를 팔로우 했습니다.");
    }

    @Override
    public String generateRedirectUrl(User sender, User recipient) {
        return "";  //todo
    }

}
