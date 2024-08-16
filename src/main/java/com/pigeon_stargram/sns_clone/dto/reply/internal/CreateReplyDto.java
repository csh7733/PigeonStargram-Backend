package com.pigeon_stargram.sns_clone.dto.reply.internal;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
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
public class CreateReplyDto implements NotificationConvertable {

    private User user;
    private Comment comment;
    private String content;

    @Override
    public Notification toNotification(User sender, User recipient) {
        return Notification.builder()
                .recipient(recipient)
                .sender(sender)
                .type(NotificationType.MY_COMMENT_REPLY)
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
        return Arrays.asList(comment.getUser().getId());
    }

    @Override
    public String generateMessage(User sender, User recipient) {
        return sender.getName() + "님이 답글을 남기셨습니다.";
    }

    @Override
    public String generateRedirectUrl(User sender, User recipient) {
        return "";
    }
}
