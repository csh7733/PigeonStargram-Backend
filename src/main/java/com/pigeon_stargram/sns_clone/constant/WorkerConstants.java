package com.pigeon_stargram.sns_clone.constant;

/**
 * 워커 스레드 및 작업 처리와 관련된 상수들을 정의한 클래스입니다.
 *
 * 이 클래스는 배치 크기와 알림, 메일 전송 작업을 처리하는 워커 스레드의 수를 관리합니다.
 */
public class WorkerConstants {

    // 배치 작업 시 처리할 항목의 개수
    public static final Integer BATCH_SIZE = 1;

    // 알림 작업을 처리하는 워커 스레드 수
    public static final Integer NOTIFICATION_WORKER_THREAD_NUM = 3;

    // 메일 전송 작업을 처리하는 워커 스레드 수
    public static final Integer MAIL_SENDER_WORKER_THREAD_NUM = 3;

    // 대량 알림 분할 작업을 처리하는 워커 스레드 수
    public static final Integer NOTIFICATION_SPLIT_WORKER_THREAD_NUM = 1;
}
