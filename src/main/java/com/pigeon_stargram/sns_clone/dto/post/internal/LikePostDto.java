package com.pigeon_stargram.sns_clone.dto.post.internal;

import com.pigeon_stargram.sns_clone.domain.notification.Notification;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationContent;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationConvertable;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationType;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.notification.internal.NotificationBatchDto;
import lombok.*;

import java.util.Arrays;
import java.util.List;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LikePostDto implements NotificationConvertable {

    private Long loginUserId;
    private String loginUserName;
    private Long postId;
    private Long writerId;

    @Override
    public Notification toNotification(User sender, User recipient) {
        return Notification.builder()
                .message(generateMessage())
                .isRead(false)
                .sender(sender)
                .recipient(recipient)
                .type(NotificationType.MY_POST_LIKE)
                .sourceId(writerId)
                .sourceId2(postId)
                .build();
    }

    @Override
    public NotificationBatchDto toNotificationBatchDto(Long senderId,
                                                       List<Long> batchRecipientIds,
                                                       Long contentId) {
        return NotificationBatchDto.builder()
                .senderId(senderId)
                .batchRecipientIds(batchRecipientIds)
                .contentId(contentId)
                .build();
    }

    @Override
    public NotificationContent toNotificationContent() {
        return NotificationContent.builder()
                .senderId(loginUserId)
                .type(NotificationType.MY_POST_LIKE)
                .message(generateMessage())
                .sourceId(writerId)
                .sourceId2(postId)
                .build();
    }

    @Override
    public Long getSenderId() {
        return loginUserId;
    }

    @Override
    public List<Long> getRecipientIds() {
        return List.of(writerId);
    }

    @Override
    public String generateMessage() {
        return loginUserName + "님이 내 글을 좋아합니다.";
    }

}
