package com.pigeon_stargram.sns_clone.worker;

import com.pigeon_stargram.sns_clone.worker.mail.MailSenderWorker;
import com.pigeon_stargram.sns_clone.worker.notification.NotificationSplitWorker;
import com.pigeon_stargram.sns_clone.worker.notification.NotificationWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.pigeon_stargram.sns_clone.constant.WorkerConstants.*;

/**
 * 애플리케이션 시작 시 각 워커를 초기화하고 실행하는 클래스입니다.
 *
 * 이 클래스는 알림 분할, 알림 전송, 메일 전송 등 다양한 작업을 처리하는 워커들을
 * 애플리케이션이 준비된 후 각각의 스레드 풀에서 실행합니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WorkerInitiator {

    private final NotificationSplitWorker notificationSplitWorker;
    private final NotificationWorker notificationWorker;
    private final MailSenderWorker mailSenderWorker;

    /**
     * 애플리케이션이 준비되면 알림 분할 워커를 실행하는 메서드입니다.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void runNotificationSplitWorker() {
        ExecutorService executorService =
                Executors.newFixedThreadPool(NOTIFICATION_SPLIT_WORKER_THREAD_NUM);

        for (int i = 0; i < NOTIFICATION_SPLIT_WORKER_THREAD_NUM; i++) {
            executorService.submit(notificationSplitWorker::acceptTask);
        }
    }

    /**
     * 애플리케이션이 준비되면 알림 전송 워커를 실행하는 메서드입니다.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void runNotificationWorker() {
        ExecutorService executorService =
                Executors.newFixedThreadPool(NOTIFICATION_WORKER_THREAD_NUM);

        for (int i = 0; i < NOTIFICATION_WORKER_THREAD_NUM; i++) {
            executorService.submit(notificationWorker::acceptTask);
        }
    }

    /**
     * 애플리케이션이 준비되면 메일 전송 워커를 실행하는 메서드입니다.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void runMailSenderWorker() {
        ExecutorService executorService =
                Executors.newFixedThreadPool(MAIL_SENDER_WORKER_THREAD_NUM);

        for (int i = 0; i < MAIL_SENDER_WORKER_THREAD_NUM; i++) {
            executorService.submit(mailSenderWorker::processTasks);
        }
    }


}
