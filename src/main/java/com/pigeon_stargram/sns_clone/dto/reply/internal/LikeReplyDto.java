package com.pigeon_stargram.sns_clone.dto.reply.internal;

import com.pigeon_stargram.sns_clone.domain.notification.NotificationContent;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationConvertable;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationType;
import com.pigeon_stargram.sns_clone.dto.notification.internal.NotificationBatchDto;
import lombok.*;

import java.util.List;

/**
 * 답글 좋아요 요청을 위한 데이터 전송 객체 (DTO)입니다.
 *
 * 이 클래스는 사용자가 답글에 좋아요를 눌렀을 때 필요한 정보를 담고 있으며,
 * 알림 전송을 위한 다양한 메서드를 구현하고 있습니다.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeReplyDto implements NotificationConvertable {

    private Long loginUserId;
    private String loginUserName;
    private Long replyId;
    private Long postUserId;
    private Long writerId;
    private Long postId;

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
                .type(NotificationType.MY_REPLY_LIKE)
                .message(generateMessage())
                .sourceId(postUserId)
                .sourceId2(postId)
                .build();
    }

    @Override
    public Long getSenderId() {
        return loginUserId;
    }

    @Override
    public List<Long> toRecipientIds() {
        return List.of(writerId);
    }

    @Override
    public String generateMessage() {
        return loginUserName + "님이 내 답글을 좋아합니다.";
    }

}
