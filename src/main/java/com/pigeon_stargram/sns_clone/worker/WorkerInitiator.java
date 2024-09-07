package com.pigeon_stargram.sns_clone.worker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.pigeon_stargram.sns_clone.constant.WorkerConstants.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class WorkerInitiator {

    private final NotificationSplitWorker notificationSplitWorker;
    private final NotificationWorker notificationWorker;
    private final MailSenderWorker mailSenderWorker;

    @EventListener(ApplicationReadyEvent.class)
    public void runNotificationSplitWorker() {
        ExecutorService executorService =
                Executors.newFixedThreadPool(NOTIFICATION_SPLIT_WORKER_THREAD_NUM);
        log.info("알림 분할 워커를 {}개의 스레드로 시작합니다.", NOTIFICATION_SPLIT_WORKER_THREAD_NUM);
        for (int i = 0; i < NOTIFICATION_SPLIT_WORKER_THREAD_NUM; i++) {
            executorService.submit(notificationSplitWorker::acceptTask);
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runNotificationWorker() {
        ExecutorService executorService =
                Executors.newFixedThreadPool(NOTIFICATION_WORKER_THREAD_NUM);
        log.info("알림 전송 워커를 {}개의 스레드로 시작합니다.", NOTIFICATION_WORKER_THREAD_NUM);
        for (int i = 0; i < NOTIFICATION_WORKER_THREAD_NUM; i++) {
            executorService.submit(notificationWorker::acceptTask);
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runMailSenderWorker() {
        ExecutorService executorService =
                Executors.newFixedThreadPool(MAIL_SENDER_WORKER_THREAD_NUM);
        log.info("메일 전송 워커를 {}개의 스레드로 시작합니다.", MAIL_SENDER_WORKER_THREAD_NUM);
        for (int i = 0; i < MAIL_SENDER_WORKER_THREAD_NUM; i++) {
            executorService.submit(mailSenderWorker::processTasks);
        }
    }


}
