package com.pigeon_stargram.sns_clone.domain.notification;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pigeon_stargram.sns_clone.domain.BaseTimeEntity;
import com.pigeon_stargram.sns_clone.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * NotificationV2 엔티티는 사용자가 수신한 알림을 나타냅니다.
 * - 알림 내용, 수신자, 읽음 여부를 저장합니다.
 * - RDB의 NOTIFICATION 테이블과 매핑됩니다.
 */
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"content"})
@EqualsAndHashCode(callSuper = false)
public class NotificationV2 extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    private NotificationContent content; // 알림의 상세 내용과 연관된 엔티티

    private Long recipientId;
    private Boolean isRead; // 알림의 읽음 여부 (true: 읽음, false: 안 읽음)

    public void setRead(Boolean read) {
        isRead = read;
    }
}