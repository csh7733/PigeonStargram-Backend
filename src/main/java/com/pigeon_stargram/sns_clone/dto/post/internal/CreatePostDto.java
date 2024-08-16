package com.pigeon_stargram.sns_clone.dto.post.internal;

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
public class CreatePostDto implements NotificationConvertable {

    private User user;
    private String content;
    private List<Long> notificationRecipientIds;

    public CreatePostDto(User user, String content) {
        this.user = user;
        this.content = content;
    }

    @Override
    public Notification toNotification(User sender, User recipient) {
        return Notification.builder()
                .recipient(recipient)
                .sender(sender)
                .type(NotificationType.FOLLOWING_POST)
                .isRead(false)
                .message(generateMessage(sender, recipient))
                .redirectUrl(generateRedirectUrl(sender, recipient))
                .build();
    }

    @Override
    public Long getSenderId() {
        return user.getId();
    }

    @Override
    public List<Long> getRecipientIds() {
        return notificationRecipientIds;
    }

    @Override
    public String generateMessage(User sender, User recipient) {
        return user.getName() + "님이 새 글을 등록했습니다.";
    }

    @Override
    public String generateRedirectUrl(User sender, User recipient) {
        return "";
    }
}
