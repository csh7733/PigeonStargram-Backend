package com.pigeon_stargram.sns_clone.worker;

import com.pigeon_stargram.sns_clone.domain.notification.NotificationContent;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationV2;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.notification.internal.NotificationBatchDto;
import com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst;
import com.pigeon_stargram.sns_clone.exception.notification.UnsupportedTypeException;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.*;
import static com.pigeon_stargram.sns_clone.worker.WorkerConstants.*;


@Slf4j
@RequiredArgsConstructor
@Component
public class RedisNotificationWorker implements NotificationWorker {

//    private final
    private final RedisService redisService;

    @Transactional
    @Scheduled(fixedRate = 100)
    @Override
    public void work() {
        Object batch = redisService.popTask(NOTIFICATION_QUEUE_KEY);
        if (!batch.getClass().isInstance(NotificationBatchDto.class)) {
            throw new UnsupportedTypeException(UNSUPPORTED_TYPE + batch.getClass());
        }

        NotificationBatchDto dto = (NotificationBatchDto) batch;
        NotificationContent content = buildNotificationContent(dto);

        List<NotificationV2> notifications = dto.getBatchRecipients().stream()
                .map(recipient -> buildNotification(recipient, content))
                .collect(Collectors.toList());



    }

    @Override
    public void enqueue(Object notification) {
        if (notification.getClass().isInstance(NotificationBatchDto.class)) {
            redisService.pushTask(NOTIFICATION_QUEUE_KEY, notification);
        } else {
            throw new UnsupportedTypeException(UNSUPPORTED_TYPE + notification.getClass());
        }
    }

    private static NotificationV2 buildNotification(User recipient,
                                                    NotificationContent content) {
        return NotificationV2.builder()
                .recipient(recipient)
                .content(content)
                .isRead(false)
                .build();
    }

    private static NotificationContent buildNotificationContent(NotificationBatchDto dto) {
        return NotificationContent.builder()
                .sender(dto.getSender())
                .senderId(dto.getSender().getId())
                .type(dto.getType())
                .message(dto.getMessage())
                .sourceId(dto.getSourceId())
                .sourceId2(dto.getSourceId2())
                .build();
    }

}
