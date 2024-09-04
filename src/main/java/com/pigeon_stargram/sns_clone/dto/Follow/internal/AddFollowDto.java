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

import static com.pigeon_stargram.sns_clone.domain.notification.NotificationType.*;

@Slf4j
@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddFollowDto implements NotificationConvertable {
    private Long senderId;
    private String senderName;
    private Long recipientId;

    public AddFollowDto(Long senderId, Long recipientId) {
        this.senderId = senderId;
        this.recipientId = recipientId;
    }

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
                .type(FOLLOW)
                .message(generateMessage())
                .sourceId(senderId)
                .build();
    }

    @Override
    public NotificationBatchDto toNotificationBatchDto(Long senderId,
                                                       List<Long> batchRecipientIds) {
        return NotificationBatchDto.builder()
                .senderId(senderId)
                .batchRecipientIds(batchRecipientIds)
                .isRead(false)
                .type(FOLLOW)
                .message(generateMessage())
                .sourceId(senderId)
                .build();
    }
    @Override
    public List<Long> getRecipientIds() {
        return Arrays.asList(recipientId);
    }

    private boolean isFollowBack(User sender, User recipient) {
        return recipient.getFollowings().stream()
                .map(Follow::getRecipient)
                .toList()
                .contains(sender);
    }

    @Override
    public String generateMessage() {
        return senderName + "님이 나를 팔로우 했습니다.";
    }

    @Override
    public String generateRedirectUrl(User sender, User recipient) {
        return "";  //todo
    }

}
