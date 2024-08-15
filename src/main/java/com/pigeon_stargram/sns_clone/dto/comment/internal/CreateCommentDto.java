package com.pigeon_stargram.sns_clone.dto.comment.internal;

import com.pigeon_stargram.sns_clone.domain.notification.Notification;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationConvertable;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationType;
import com.pigeon_stargram.sns_clone.domain.post.Posts;
import com.pigeon_stargram.sns_clone.domain.user.User;
import lombok.*;

import java.util.Arrays;
import java.util.List;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentDto implements NotificationConvertable {

    private User user;
    private Posts post;
    private String content;

    @Override
    public Notification toNotification(User sender, User recipient) {
        return Notification.builder()
                .type(NotificationType.MY_POST_COMMENT)
                .message(generateMessage(sender, recipient))
                .isRead(false)
                .recipient(recipient)
                .sender(sender)
                .redirectUrl(generateRedirectUrl(sender, recipient))
                .build();
    }

    @Override
    public Long getSenderId() {
        return user.getId();
    }

    @Override
    public List<Long> getRecipientIds() {
        return Arrays.asList(post.getUser().getId());
    }

    @Override
    public String generateMessage(User sender, User recipient) {
        return sender.getName() + "님이 댓글을 남겼어요.";
    }

    @Override
    public String generateRedirectUrl(User sender, User recipient) {
        return "";  //todo
    }
}

