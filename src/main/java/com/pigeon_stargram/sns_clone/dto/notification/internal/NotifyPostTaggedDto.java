package com.pigeon_stargram.sns_clone.dto.notification.internal;

import com.pigeon_stargram.sns_clone.domain.notification.NotificationContent;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationConvertable;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationType;
import lombok.*;

import java.util.List;

/**
 * 게시물 태그 알림을 위한 데이터 전송 객체 (DTO)입니다.
 *
 * 이 클래스는 게시물에서 태그된 사용자에게 알림을 전송할 때 필요한 정보를 담고 있으며,
 * 알림 전송을 위한 다양한 메서드를 구현하고 있습니다.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotifyPostTaggedDto implements NotificationConvertable {

    private Long userId;
    private String userName;
    private String content;
    private List<Long> notificationRecipientIds;

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
                .senderId(userId)
                .type(NotificationType.POST_TAG)
                .message(generateMessage())
                .sourceId(userId)
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
        return userName + "님이 새 글에서 당신을 언급했습니다. 지금 " +
                userName +"님의 프로필로 가서 확인하세요!";
    }

}
