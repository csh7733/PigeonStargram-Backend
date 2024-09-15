package com.pigeon_stargram.sns_clone.constant;

/**
 * Redis에서 메시지 큐 처리를 위한 상수들을 정의한 클래스입니다.
 *
 * 이 클래스는 메일, 알림 등과 관련된 Redis 큐의 키를 정의합니다.
 */
public class RedisQueueConstants {

    // 메일 전송을 처리하는 Redis 큐 키
    public static final String MAIL_QUEUE = "mailQueue";

    // 알림 전송을 처리하는 Redis 큐 키
    public static final String NOTIFICATION_QUEUE = "notificationQueue";
}
