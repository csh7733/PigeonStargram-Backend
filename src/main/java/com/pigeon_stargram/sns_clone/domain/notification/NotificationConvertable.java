package com.pigeon_stargram.sns_clone.domain.notification;

import com.pigeon_stargram.sns_clone.dto.notification.internal.NotificationBatchDto;

import java.util.List;

/**
 * 알림 전송과 관련된 객체가 구현해야 하는 인터페이스입니다.
 *
 * 이 인터페이스는 알림 관련 객체가 공통적으로 제공해야 하는 메서드를 정의합니다.
 * 알림의 배치 전송, 콘텐츠 생성, 수신자 리스트 반환, 메시지 생성 등을 처리할 수 있도록 합니다.
 */
public interface NotificationConvertable {

    /**
     * 알림 배치 DTO를 생성합니다.
     *
     * @param senderId 팔로우를 요청한 사용자 ID
     * @param batchRecipientIds 알림을 받을 사용자 ID 리스트
     * @param contentId 알림 콘텐츠 ID
     * @return 생성된 NotificationBatchDto 객체
     */
    NotificationBatchDto toNotificationBatchDto(Long senderId, List<Long> batchRecipientIds, Long contentId);

    /**
     * 알림 콘텐츠를 생성합니다.
     *
     * @return 생성된 NotificationContent 객체
     */
    NotificationContent toNotificationContent();

    /**
     * 발신자 ID를 반환합니다.
     *
     * @return 발신자 ID
     */
    Long getSenderId();

    /**
     * 알림을 받을 사용자 ID 리스트를 반환합니다.
     *
     * @return 수신자 ID 리스트
     */
    List<Long> toRecipientIds();

    /**
     * 알림의 메시지를 생성합니다.
     *
     * @return 생성된 알림 메시지
     */
    String generateMessage();
}
