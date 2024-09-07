package com.pigeon_stargram.sns_clone.worker;

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

@Slf4j
@RequiredArgsConstructor
@Component
public class MemoryNotificationWorker implements NotificationWorker {

    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    private final Queue<ResponseNotificationDto> queue = new LinkedList<>();
    private final SimpMessagingTemplate messagingTemplate;


    @PostConstruct
    public void startWorkers() {
        log.info("알림 전송 워커를 {}개의 스레드로 시작합니다.", 3);
        for (int i = 0; i < 3; i++) {
            executorService.submit(this::acceptTask);
        }
    }

    @Override
    public void acceptTask() {
        Optional.ofNullable(queue.poll())
                .ifPresent(task -> {
                    work(task);

                });
    }

    @Transactional
    @Override
    public void work(Object task) {
        ResponseNotificationDto notification = (ResponseNotificationDto) task;

        String destination = "/topic/notification/" + notification.getTargetUserId();
        messagingTemplate.convertAndSend(destination, notification);
        log.info("notification sent = {}", notification);
    }

    @Override
    public void enqueue(Object notification) {
        if (notification instanceof ResponseNotificationDto) {
            queue.add((ResponseNotificationDto) notification);
        } else {
            throw new UnsupportedTypeException(UNSUPPORTED_TYPE + notification.getClass());
        }
    }
}
