package com.pigeon_stargram.sns_clone.dto.post.internal;

import com.pigeon_stargram.sns_clone.domain.notification.Notification;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationConvertable;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationType;
import com.pigeon_stargram.sns_clone.domain.post.Image;
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
    private List<String> imageUrls;
    private Boolean hasImage;

    public CreatePostDto(User user, String content) {
        this.user = user;
        this.content = content;
        this.hasImage = false;
    }

    public CreatePostDto(User user, String content,List<Long> notificationRecipientIds) {
        this.user = user;
        this.content = content;
        this.notificationRecipientIds = notificationRecipientIds;
        this.hasImage = false;
    }

    @Override
    public Notification toNotification(User sender, User recipient) {
        return Notification.builder()
                .recipient(recipient)
                .sender(sender)
                .type(NotificationType.FOLLOWING_POST)
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
        return sender.getName() + "님이 새 글을 등록했습니다. 지금 " +
                sender.getName() +"님의 프로필로 가서 확인하세요!";
    }

    @Override
    public String generateRedirectUrl(User sender, User recipient) {
        return "";
    }
}
