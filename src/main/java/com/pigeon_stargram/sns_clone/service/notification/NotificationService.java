package com.pigeon_stargram.sns_clone.service.notification;

import com.pigeon_stargram.sns_clone.domain.notification.NotificationConvertable;
import com.pigeon_stargram.sns_clone.dto.notification.response.ResponseNotificationDto;

import java.util.List;

/**
 * 알림 관련 비즈니스 로직을 처리하는 Service 인터페이스.
 * 알림 전송, 조회, 읽음 처리 등을 위한 메서드를 제공합니다.
 */
public interface NotificationService {

    /**
     * 알림 전송을 위한 작업을 큐에 넣습니다.
     * @param dto 알림 내용과 관련된 데이터
     */
    void sendToSplitWorker(NotificationConvertable dto);

    /**
     * 특정 사용자의 알림 목록을 조회합니다.
     * @param userId 사용자 ID
     * @return 알림 DTO 리스트
     */
    List<ResponseNotificationDto> findByUserId(Long userId);

    /**
     * 특정 알림을 읽음 상태로 설정합니다.
     * @param notificationId 알림 ID
     */
    void readNotification(Long notificationId);

    /**
     * 특정 사용자의 모든 알림을 읽음 상태로 설정합니다.
     * @param userId 사용자 ID
     */
    void readNotifications(Long userId);

    /**
     * 특정 알림을 삭제합니다.
     * @param notificationId 알림 ID
     */
    void deleteNotification(Long notificationId);

    /**
     * 특정 사용자의 모든 알림을 삭제합니다.
     * @param recipientId 사용자 ID
     */
    void deleteAll(Long recipientId);

    /**
     * 태그된 사용자에게 알림을 전송합니다.
     * @param dto 알림 내용과 관련된 데이터
     */
    void notifyTaggedUsers(NotificationConvertable dto);
}
