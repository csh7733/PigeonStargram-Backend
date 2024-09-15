package com.pigeon_stargram.sns_clone.dto.notification.internal;

import com.pigeon_stargram.sns_clone.domain.notification.NotificationContent;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationConvertable;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationType;
import lombok.*;

import java.util.List;

/**
 * 댓글 태그 알림을 위한 데이터 전송 객체 (DTO)입니다.
 *
 * 이 클래스는 댓글에서 태그된 사용자에게 알림을 전송할 때 필요한 정보를 담고 있으며,
 * 알림 전송을 위한 다양한 메서드를 구현하고 있습니다.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotifyCommentTaggedDto implements NotificationConvertable {

    private Long userId;
    private String userName;
    private String content;
    private Long postUserId;
    private Long postId;
    private List<Long> notificationRecipientIds;

    @Override
    public NotificationBatchDto toNotificationBatchDto(Long senderId,
                                                       List<Long> recipientIds,
                                                       Long contentId) {
        return NotificationBatchDto.builder()
                .senderId(senderId)
                .batchRecipientIds(recipientIds)
                .contentId(contentId)
                .build();
    }

    @Override
    public NotificationContent toNotificationContent() {
        return NotificationContent.builder()
                .senderId(userId)
                .type(NotificationType.COMMENT_TAG)
                .message(generateMessage())
                .sourceId(postUserId)
                .sourceId2(postId)
                .build();
    }

    @Override
    public Long getSenderId() {
        return userId;
    }

    @Override
    public List<Long> toRecipientIds() {
        return notificationRecipientIds;
    }

    @Override
    public String generateMessage() {
        return userName + "님이 댓글에서 당신을 언급했습니다.";
    }

}
