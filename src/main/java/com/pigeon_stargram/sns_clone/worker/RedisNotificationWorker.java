package com.pigeon_stargram.sns_clone.worker;

import com.pigeon_stargram.sns_clone.dto.notification.internal.NotificationBatchDto;
import com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst;
import com.pigeon_stargram.sns_clone.exception.notification.UnsupportedTypeException;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.*;
import static com.pigeon_stargram.sns_clone.worker.WorkerConstants.*;


@Slf4j
@RequiredArgsConstructor
@Component
public class RedisNotificationWorker implements NotificationWorker {

    private final RedisService redisService;

    @Transactional
    @Scheduled(fixedRate = 100)
    @Override
    public void work() {

    }

    @Override
    public void enqueue(Object notification) {
        if (notification.getClass().isInstance(NotificationBatchDto.class)) {
            redisService.pushTask(NOTIFICATION_QUEUE_KEY, notification);
        } else {
            throw new UnsupportedTypeException(UNSUPPORTED_TYPE + notification.getClass());
        }
    }
}
