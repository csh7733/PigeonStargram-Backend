package com.pigeon_stargram.sns_clone.dto.post.internal;

import com.pigeon_stargram.sns_clone.domain.notification.Notification;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationConvertable;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationType;
import com.pigeon_stargram.sns_clone.domain.post.Image;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.notification.internal.NotificationBatchDto;
import lombok.*;

import java.util.List;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostDto implements NotificationConvertable {

    private Long loginUserId;
    private String loginUserName;
    private String content;
    private List<Long> notificationRecipientIds;
    private List<String> imageUrls;
    private String fieldKey;
    private Boolean hasImage;
    private List<Long> taggedUserIds;

    public CreatePostDto(Long loginUserId,
                         String content,
                         List<Long> notificationRecipientIds,
                         List<Long> taggedUserIds) {
        this.loginUserId = loginUserId;
        this.content = content;
        this.notificationRecipientIds = notificationRecipientIds;
        this.taggedUserIds = taggedUserIds;
        this.hasImage = false;
    }

    @Override
    public Notification toNotification(User sender, User recipient) {
        return Notification.builder()
                .recipient(recipient)
                .sender(sender)
                .type(NotificationType.FOLLOWING_POST)
                .isRead(false)
                .message(generateMessage())
                .sourceId(loginUserId)
                .build();
    }

    @Override
    public NotificationBatchDto toNotificationBatchDto(Long senderId, List<Long> batchRecipientIds) {
        return NotificationBatchDto.builder()
                .batchRecipientIds(batchRecipientIds)
                .senderId(senderId)
                .type(NotificationType.FOLLOWING_POST)
                .isRead(false)
                .message(generateMessage())
                .sourceId(loginUserId)
                .build();
    }

    @Override
    public Long getSenderId() {
        return loginUserId;
    }

    @Override
    public List<Long> getRecipientIds() {
        return notificationRecipientIds;
    }

    @Override
    public String generateMessage() {
        return loginUserName + "님이 새 글을 등록했습니다. 지금 " +
                loginUserName +"님의 프로필로 가서 확인하세요!";
    }

    @Override
    public String generateRedirectUrl(User sender, User recipient) {
        return "";
    }
}
