package com.pigeon_stargram.sns_clone.service.notification;

import com.pigeon_stargram.sns_clone.domain.notification.NotificationContent;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationV2;

import java.util.List;

/**
 * 알림 관련 비즈니스 로직을 처리하는 Service 인터페이스.
 * 알림 저장, 조회, 삭제 등의 작업을 위한 메서드를 제공합니다.
 */
public interface NotificationCrudService {

    /**
     * 특정 수신자의 알림 목록을 조회합니다.
     * 
     * @param recipientId 수신자 ID
     * @return 조회된 알림 목록
     */
    List<NotificationV2> findNotificationByRecipientId(Long recipientId);

    /**
     * 알림 ID로 알림을 조회합니다.
     *
     * @param notificationId 알림 ID
     * @return 조회된 알림 객체
     */
    NotificationV2 findById(Long notificationId);

    /**
     * 알림 내용(Content)을 ID로 조회합니다.
     *
     * @param contentId 알림 내용 ID
     * @return 조회된 알림 내용 객체
     */
    NotificationContent findContentById(Long contentId);

    /**
     * 새로운 알림을 리스트로 전부 저장합니다.
     *
     * @param notifications 저장할 알림 객체 리스트
     * @return 저장된 알림 객체 리스트
     */
    List<NotificationV2> saveAll(List<NotificationV2> notifications);

    /**
     * 알림 내용을 저장합니다.
     *
     * @param content 저장할 알림 내용 객체
     * @return 저장된 알림 내용 객체
     */
    NotificationContent saveContent(NotificationContent content);

    /**
     * 알림 ID로 알림을 삭제합니다.
     *
     * @param notificationId 삭제할 알림 ID
     */
    void deleteNotificationById(Long notificationId);

    /**
     * 특정 수신자의 모든 알림을 삭제합니다.
     *
     * @param recipientId 삭제할 알림의 수신자 ID
     */
    void deleteAllNotificationByRecipientId(Long recipientId);
}
