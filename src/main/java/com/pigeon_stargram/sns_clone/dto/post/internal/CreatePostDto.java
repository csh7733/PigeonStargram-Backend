package com.pigeon_stargram.sns_clone.dto.post.internal;

import com.pigeon_stargram.sns_clone.domain.notification.Notification;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationContent;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationConvertable;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationType;
import com.pigeon_stargram.sns_clone.domain.post.Image;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.notification.internal.NotificationBatchDto;
import lombok.*;

import java.util.List;

/**
 * CreatePostDto는 게시물 생성과 관련된 데이터를 담고 있으며,
 * 알림(Notification) 시스템에서 사용할 데이터를 변환하는 기능을 제공합니다.
 * NotificationConvertable 인터페이스를 구현하여 알림 배치 데이터 및 알림 내용을
 * 생성할 수 있는 메서드를 포함하고 있습니다.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreatePostDto implements NotificationConvertable {

    private Long loginUserId;
    private String loginUserName;
    private String content;
    private Boolean hasImage;
    private List<String> imageUrls;

    private List<Long> notificationRecipientIds;
    private List<Long> taggedUserIds;
    
    // 이미지 업로드 완료 전에 작성자 이외의 접근을 막을 때 사용되는 Hash Field Key
    private String fieldKey;

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
                .type(NotificationType.FOLLOWING_POST)
                .message(generateMessage())
                .sourceId(loginUserId)
                .build();
    }

    @Override
    public Long getSenderId() {
        return loginUserId;
    }

    @Override
    public List<Long> toRecipientIds() {
        return notificationRecipientIds;
    }

    @Override
    public String generateMessage() {
        return loginUserName + "님이 새 글을 등록했습니다. 지금 " +
                loginUserName +"님의 프로필로 가서 확인하세요!";
    }


}
