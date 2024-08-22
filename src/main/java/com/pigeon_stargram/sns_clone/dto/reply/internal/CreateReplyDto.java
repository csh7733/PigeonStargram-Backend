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

    private Long loginUserId;
    private Long commentUserId;
    private Long commentId;
    private String content;
    private Long postUserId;
    private Long postId;
    private List<Long> taggedUserIds;

    @Override
    public Notification toNotification(User sender, User recipient) {
        return Notification.builder()
                .recipient(recipient)
                .sender(sender)
                .type(NotificationType.MY_COMMENT_REPLY)
                .isRead(false)
                .message(generateMessage(sender, recipient))
                .sourceId(postUserId)
                .sourceId2(postId)
                .build();
    }

    @Override
    public Long getSenderId() {
        return loginUserId;
    }

    @Override
    public List<Long> getRecipientIds() {
        return List.of(commentUserId);
    }

    @Override
    public String generateMessage(User sender, User recipient) {
        return sender.getName() + "님이 답글을 남겼습니다.";
    }

    @Override
    public String generateRedirectUrl(User sender, User recipient) {
        return "";
    }
}
