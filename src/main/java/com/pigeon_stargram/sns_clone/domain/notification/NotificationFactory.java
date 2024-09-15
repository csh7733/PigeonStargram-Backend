package com.pigeon_stargram.sns_clone.domain.notification;

/**
 * NotificationFactory 클래스는 NotificationV2 객체를 생성하는 Factory 역할을 합니다.
 * - NotificationV2 객체를 생성하는 메서드를 제공합니다.
 */
public class NotificationFactory {

    /**
     * NotificationV2 객체를 생성하는 메서드입니다.
     *
     * @param recipientId 알림을 받는 사용자 ID
     * @param content     알림의 상세 내용(NotificationContent)
     * @return NotificationV2 객체
     */
    public static NotificationV2 createNotification(Long recipientId, NotificationContent content) {
        return NotificationV2.builder()
                .recipientId(recipientId)
                .content(content)
                .isRead(false)
                .build();
    }
}
