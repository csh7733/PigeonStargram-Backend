package com.pigeon_stargram.sns_clone.worker;

import com.pigeon_stargram.sns_clone.domain.notification.Notification;
import com.pigeon_stargram.sns_clone.dto.notification.response.ResponseNotificationDto;
import com.pigeon_stargram.sns_clone.repository.notification.NotificationRepository;
import com.pigeon_stargram.sns_clone.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

@Slf4j
@RequiredArgsConstructor
@Component
public class MemoryNotificationWorker implements NotificationWorker{

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
                    log.info("notification sent = {}", notification.toString());
                });
    }

    @Override
    public void enqueue(ResponseNotificationDto notification) {
        queue.add(notification);
    }
}
