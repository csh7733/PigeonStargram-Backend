package com.pigeon_stargram.sns_clone.worker;

import com.pigeon_stargram.sns_clone.domain.notification.Notification;
import com.pigeon_stargram.sns_clone.repository.notification.NotificationRepository;
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

    private final Queue<Notification> queue = new LinkedList<>();
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    @Scheduled(fixedRate = 1000)
    public void work() {
        Optional.ofNullable(queue.poll())
                .ifPresent(front -> {
                    Notification notification = notificationRepository.findById(front.getId()).get();
                    String destination = "/topic/notification/" + notification.getRecipient().getId();
                    messagingTemplate.convertAndSend(destination, notification);
                    log.info("notification sent = {}", notification);
                });
    }

    @Override
    public void enqueue(Notification notification) {
        queue.add(notification);
    }
}
