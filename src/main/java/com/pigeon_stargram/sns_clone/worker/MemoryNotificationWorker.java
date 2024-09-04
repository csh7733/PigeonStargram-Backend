package com.pigeon_stargram.sns_clone.worker;

import com.pigeon_stargram.sns_clone.dto.notification.response.ResponseNotificationDto;
import com.pigeon_stargram.sns_clone.exception.redis.UnsupportedTypeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.UNSUPPORTED_TYPE;

@Slf4j
@RequiredArgsConstructor
@Component
public class MemoryNotificationWorker implements NotificationWorker {

    private final Queue<ResponseNotificationDto> queue = new LinkedList<>();
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    @Scheduled(fixedRate = 100)
    public void work() {
        Optional.ofNullable(queue.poll())
                .ifPresent(notification -> {
                    String destination = "/topic/notification/" + notification.getTargetUserId();
                    messagingTemplate.convertAndSend(destination, notification);
                    log.info("notification sent = {}", notification);
                });
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
