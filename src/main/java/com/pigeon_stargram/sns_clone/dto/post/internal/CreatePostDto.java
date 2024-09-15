package com.pigeon_stargram.sns_clone.dto.post.internal;

import com.pigeon_stargram.sns_clone.domain.notification.NotificationContent;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationConvertable;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationType;
import com.pigeon_stargram.sns_clone.dto.notification.internal.NotificationBatchDto;
import lombok.*;

import java.util.List;

/**
 * 게시물 생성 요청을 위한 데이터 전송 객체 (DTO)입니다.
 *
 * 이 클래스는 사용자가 게시물을 생성할 때 필요한 정보를 담고 있으며,
 * 알림 전송을 위한 다양한 메서드를 구현하고 있습니다.
 */
@Getter
@Setter
@Builder
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
