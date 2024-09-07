package com.pigeon_stargram.sns_clone.worker;

import com.pigeon_stargram.sns_clone.constant.RedisQueueConstants;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationContent;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationV2;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.notification.internal.NotificationBatchDto;
import com.pigeon_stargram.sns_clone.dto.notification.response.ResponseNotificationDto;
import com.pigeon_stargram.sns_clone.exception.redis.UnsupportedTypeException;
import com.pigeon_stargram.sns_clone.repository.notification.NotificationContentRepository;
import com.pigeon_stargram.sns_clone.service.notification.NotificationBuilder;
import com.pigeon_stargram.sns_clone.service.notification.NotificationCrudService;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.constant.RedisQueueConstants.*;
import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.*;
import static com.pigeon_stargram.sns_clone.service.notification.NotificationBuilder.*;
import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.*;
import static com.pigeon_stargram.sns_clone.constant.WorkerConstants.*;


@Primary
@Slf4j
@RequiredArgsConstructor
@Component
public class RedisNotificationWorker implements NotificationWorker {

    private final RedisService redisService;
    private final NotificationCrudService notificationCrudService;
    private final UserService userService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    private final SimpMessagingTemplate messagingTemplate;

    @PostConstruct
    @Override
    public void startWorkers() {
        log.info("알림 전송 워커를 {}개의 스레드로 시작합니다.", 3);
        for (int i = 0; i < 3; i++) {
            executorService.submit(this::work);
        }
    }

    @Transactional
    @Override
    public void work() {

        Object batch = redisService.popTask(NOTIFICATION_QUEUE_KEY, Duration.ofSeconds(5));
        if(batch == null) {
            return;
        } else if (!(batch instanceof NotificationBatchDto)) {
            throw new UnsupportedTypeException(UNSUPPORTED_TYPE + batch.getClass());
        }

        NotificationBatchDto dto = (NotificationBatchDto) batch;

        log.info("contentId={}", dto.getContentId());

        NotificationContent content = notificationCrudService.findContentById(dto.getContentId());

        User sender = userService.findById(content.getSenderId());

        List<NotificationV2> notifications = dto.getBatchRecipientIds().stream()
                .map(recipientId -> buildNotification(recipientId, content))
                .collect(Collectors.toList());

        notifications.forEach(notification -> {
            ResponseNotificationDto message = saveNotificationAndbuildMessage(notification, sender);
            sendMessage(message);
        });

    }

    private ResponseNotificationDto saveNotificationAndbuildMessage(NotificationV2 notification,
                                                                    User sender) {
        NotificationV2 save = notificationCrudService.save(notification);
        NotificationContent saveContent = save.getContent();
        ResponseNotificationDto message =
                buildResponseNotificationDto(save, sender, saveContent);
        return message;
    }

    private void sendMessage(ResponseNotificationDto message) {
        String destination = "/topic/notification/" + message.getTargetUserId();
        messagingTemplate.convertAndSend(destination, message);
        log.info("notification sent = {}", message);
    }

    @Override
    public void enqueue(Object notification) {
        if (notification instanceof NotificationBatchDto) {
            redisService.pushTask(NOTIFICATION_QUEUE_KEY, notification);
        } else {
            throw new UnsupportedTypeException(UNSUPPORTED_TYPE + notification.getClass());
        }
    }
}
