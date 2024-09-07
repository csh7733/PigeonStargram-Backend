package com.pigeon_stargram.sns_clone.dto.comment.internal;

import com.pigeon_stargram.sns_clone.domain.notification.Notification;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationContent;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationConvertable;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationType;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.notification.internal.NotificationBatchDto;
import lombok.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.pigeon_stargram.sns_clone.domain.notification.NotificationType.FOLLOW;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentDto implements NotificationConvertable {

    private Long loginUserId;
    private String loginUserName;
    private Long postId;
    private Long postUserId;
    private String context;
    private String content;
    private List<Long> taggedUserIds;

    public CreateCommentDto(Long loginUserId, Long postId, Long postUserId, String content, List<Long> taggedUserIds) {
        this.loginUserId = loginUserId;
        this.postId = postId;
        this.postUserId = postUserId;
        this.content = content;
        this.taggedUserIds = taggedUserIds;
    }

    @Override
    public Notification toNotification(User sender,
                                       User recipient) {
        return Notification.builder()
                .type(NotificationType.MY_POST_COMMENT)
                .message(generateMessage())
                .isRead(false)
                .recipient(recipient)
                .sender(sender)
                .sourceId(postUserId)
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
                .type(NotificationType.MY_POST_COMMENT)
                .message(generateMessage())
                .sourceId(postUserId)
                .build();
    }


    @Override
    public Long getSenderId() {
        return loginUserId;
    }

    @Override
    public List<Long> toRecipientIds() {
        return List.of(postUserId);
    }

    @Override
    public String generateMessage() {
        return loginUserName + "님이 댓글을 남겼습니다.";
    }

}

