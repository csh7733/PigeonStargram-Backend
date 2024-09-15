package com.pigeon_stargram.sns_clone.worker.notification;

import com.pigeon_stargram.sns_clone.dto.notification.response.ResponseNotificationDto;
import com.pigeon_stargram.sns_clone.exception.redis.UnsupportedTypeException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.UNSUPPORTED_TYPE;

/**
 * 메모리 방식으로 알림을 처리하는 워커 클래스입니다.
 *
 * 이 클래스는 V1에서 사용된 방식으로, 메모리 내에서 알림을 처리하며,
 * V2에서는 더 이상 사용되지 않는 방식입니다.
 *
 * ExecutorService를 사용하여 멀티스레드 환경에서 알림 작업을 처리합니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MemoryNotificationWorker implements NotificationWorker {

    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    private final Queue<ResponseNotificationDto> queue = new LinkedList<>();
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 초기화 시 여러 스레드를 사용해 알림 작업을 처리하는 워커를 시작합니다.
     */
    @PostConstruct
    public void startWorkers() {
        log.info("알림 전송 워커를 {}개의 스레드로 시작합니다.", 3);
        for (int i = 0; i < 3; i++) {
            executorService.submit(this::acceptTask);
        }
    }

    /**
     * 큐에서 알림 작업을 가져와 처리하는 메서드입니다.
     */
    @Override
    public void acceptTask() {
        Optional.ofNullable(queue.poll())
                .ifPresent(task -> {
                    work(task);

                });
    }

    /**
     * 주어진 알림 작업을 처리하는 메서드입니다.
     *
     * @param task 처리할 알림 작업
     */
    @Transactional
    @Override
    public void work(Object task) {
        ResponseNotificationDto notification = (ResponseNotificationDto) task;

        String destination = "/topic/notification/" + notification.getTargetUserId();
        messagingTemplate.convertAndSend(destination, notification);
        log.info("notification sent = {}", notification);
    }

    /**
     * 알림 작업을 큐에 추가하는 메서드입니다.
     *
     * @param notification 큐에 추가할 알림 작업
     */
    @Override
    public void enqueue(Object notification) {
        if (notification instanceof ResponseNotificationDto) {
            queue.add((ResponseNotificationDto) notification);
        } else {
            throw new UnsupportedTypeException(UNSUPPORTED_TYPE + notification.getClass());
        }
    }
}
