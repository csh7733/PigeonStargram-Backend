package com.pigeon_stargram.sns_clone.dto.Follow.internal;

import com.pigeon_stargram.sns_clone.domain.follow.Follow;
import com.pigeon_stargram.sns_clone.domain.notification.Notification;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationConvertable;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationType;
import com.pigeon_stargram.sns_clone.domain.user.User;
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
                .type(NotificationType.FOLLOW)
                .message(generateMessage(sender, recipient))
                .sourceId(senderId)
                .build();
    }

    @Override
    public List<Long> getRecipientIds() {
        return Arrays.asList(recipientId);
    }

    @Override
    public String generateMessage(User sender, User recipient) {
        return sender.getName() + "님이 나를 팔로우 했습니다.";
    }

    @Override
    public String generateRedirectUrl(User sender, User recipient) {
        return "";  //todo
    }

}
