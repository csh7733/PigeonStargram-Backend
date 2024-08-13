package com.pigeon_stargram.sns_clone.dto.post;

import com.pigeon_stargram.sns_clone.domain.notification.Notification;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationConvertable;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationType;
import com.pigeon_stargram.sns_clone.domain.user.User;
import lombok.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LikePostDto implements NotificationConvertable {

    private User user;
    private Long postId;
    private Long writerId;

    public LikePostDto(User user, Long postId) {
        this.user = user;
        this.postId = postId;
    }

    @Override
    public Notification toNotification(User sender, User recipient) {
        return Notification.builder()
                .redirectUrl(generateRedirectUrl(sender, recipient))
                .message(generateMessage(sender, recipient))
                .isRead(false)
                .sender(sender)
                .recipient(recipient)
                .type(NotificationType.MY_POST_LIKE)
                .build();
    }

    @Override
    public Long getSenderId() {
        return user.getId();
    }

    @Override
    public List<Long> getRecipientIds() {
        return Arrays.asList(writerId);
    }

    @Override
    public String generateMessage(User sender, User recipient) {
        return sender.getName() + "님이 내 글을 좋아합니다.";
    }

    @Override
    public String generateRedirectUrl(User sender, User recipient) {
        return "";
    }
}
