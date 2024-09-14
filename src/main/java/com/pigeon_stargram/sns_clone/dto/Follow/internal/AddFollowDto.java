package com.pigeon_stargram.sns_clone.dto.Follow.internal;

import com.pigeon_stargram.sns_clone.domain.notification.NotificationContent;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationConvertable;
import com.pigeon_stargram.sns_clone.dto.notification.internal.NotificationBatchDto;
import lombok.*;

import java.util.Arrays;
import java.util.List;

import static com.pigeon_stargram.sns_clone.domain.notification.NotificationType.*;

/**
 * 팔로우 추가 요청을 위한 데이터 전송 객체 (DTO)입니다.
 *
 * 이 클래스는 사용자 간의 팔로우 관계를 생성하기 위한 정보를 담고 있으며,
 * 알림 전송을 위한 다양한 메서드를 구현하고 있습니다.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AddFollowDto implements NotificationConvertable {

    private Long senderId;
    private String senderName;
    private Long recipientId;

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
                .senderId(senderId)
                .type(FOLLOW) // 알림 유형을 팔로우로 설정
                .message(generateMessage()) // 알림 메시지 생성
                .sourceId(senderId)
                .build();
    }

    @Override
    public List<Long> toRecipientIds() {
        return Arrays.asList(recipientId); // 단일 수신자 ID를 포함한 리스트 반환
    }

    @Override
    public String generateMessage() {
        return senderName + "님이 나를 팔로우 했습니다."; // 메시지 포맷
    }
}