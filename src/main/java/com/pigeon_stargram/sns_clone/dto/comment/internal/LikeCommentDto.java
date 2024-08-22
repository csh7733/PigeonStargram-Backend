package com.pigeon_stargram.sns_clone.dto.comment.internal;

import com.pigeon_stargram.sns_clone.domain.notification.Notification;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationConvertable;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationType;
import com.pigeon_stargram.sns_clone.domain.user.User;
import lombok.*;

import java.util.Arrays;
import java.util.List;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LikeCommentDto implements NotificationConvertable {

    private Long loginUserId;
    private Long commentId;
    private Long postUserId;
    private Long writerId;
    private Long postId;

    @Override
    public Notification toNotification(User sender, User recipient) {
        return Notification.builder()
                .type(NotificationType.MY_COMMENT_LIKE)
                .message(generateMessage(sender, recipient))
                .isRead(false)
                .sender(sender)
                .recipient(recipient)
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
        return Arrays.asList(writerId);
    }

    @Override
    public String generateMessage(User sender, User recipient) {
        return sender.getName() + "님이 내 댓글을 좋아합니다.";
    }

    @Override
    public String generateRedirectUrl(User sender, User recipient) {
        return "";
    }
}
