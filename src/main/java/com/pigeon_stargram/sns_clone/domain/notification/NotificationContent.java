package com.pigeon_stargram.sns_clone.domain.notification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pigeon_stargram.sns_clone.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
/**
 * NotificationContent 엔티티는 사용자 간 알림 메시지를 나타냅니다.
 * - 알림 발신자, 알림 유형, 알림 메시지, 그리고 관련된 소스 ID들을 저장합니다.
 * - RDB의 NOTIFICATION_CONTENT 테이블과 매핑됩니다.
 */
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class NotificationContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderId;
    private NotificationType type; // 알림의 유형 (예: 댓글, 좋아요 등)
    private String message; // 알림 내용
    private Long sourceId; // 알림을 눌렀을때 이동하도록 필요한 첫 번째 관련 소스 (예: 유저 ID)
    private Long sourceId2; // 알림을 눌렀을때 이동하도록 필요한 두 번째 관련 소스 (예: 게시물 ID)

}
