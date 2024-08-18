package com.pigeon_stargram.sns_clone.dto.notification.internal;

import com.pigeon_stargram.sns_clone.domain.notification.Notification;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationConvertable;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationType;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.post.request.RequestCreatePostDto;
import lombok.*;

import java.util.List;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotifyPostTaggedUsersDto implements NotificationConvertable {

    private User user;
    private String content;
    private List<Long> notificationRecipientIds;

    @Override
    public Notification toNotification(User sender, User recipient) {
        return Notification.builder()
                .recipient(recipient)
                .sender(sender)
                .type(NotificationType.POST_TAG)
                .isRead(false)
                .message(generateMessage(sender, recipient))
                .sourceId(user.getId())
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
        return user.getName() + "님이 새 글에서 당신을 언급했습니다. 지금 " +
                user.getName() +"님의 프로필로 가서 확인하세요!";
    }

    @Override
    public String generateRedirectUrl(User sender, User recipient) {
        return "";
    }
}
