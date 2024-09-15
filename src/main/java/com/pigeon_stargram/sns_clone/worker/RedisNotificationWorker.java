package com.pigeon_stargram.sns_clone.worker;

import com.pigeon_stargram.sns_clone.domain.notification.NotificationContent;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationV2;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.notification.internal.NotificationBatchDto;
import com.pigeon_stargram.sns_clone.dto.notification.response.ResponseNotificationDto;
import com.pigeon_stargram.sns_clone.exception.redis.UnsupportedTypeException;
import com.pigeon_stargram.sns_clone.service.notification.NotificationCrudService;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import io.lettuce.core.RedisConnectionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.constant.RedisQueueConstants.NOTIFICATION_QUEUE;
import static com.pigeon_stargram.sns_clone.domain.notification.NotificationFactory.createNotification;
import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.UNSUPPORTED_TYPE;
import static com.pigeon_stargram.sns_clone.dto.notification.NotificationDtoConvertor.buildResponseNotificationDto;


@Primary
@Slf4j
@RequiredArgsConstructor
@Component
public class RedisNotificationWorker implements NotificationWorker {

    private final RedisService redisService;
    private final NotificationCrudService notificationCrudService;
    private final UserService userService;

    private final SimpMessagingTemplate messagingTemplate;


    @Override
    public void acceptTask() {
        while (true) {
            try {
                log.info("Redis 큐에서 알림 전송 작업을 대기 중입니다...");
                // Redis 작업큐에서 Blocking Pop 방식으로 가져옴
                Object task = redisService.popTask(NOTIFICATION_QUEUE);
                if (task == null) {
                    throw new QueryTimeoutException("");
                } else if (!(task instanceof NotificationBatchDto)) {
                    throw new UnsupportedTypeException(UNSUPPORTED_TYPE + task.getClass());
                }
                NotificationBatchDto batch = (NotificationBatchDto) task;

                // 가져온 작업이 유효하다면 메일을 전송
                log.info("알림 작업을 가져왔습니다. 수신자: {}", batch.getBatchRecipientIds());
                if (batch.getBatchRecipientIds().isEmpty()) {
                    log.info("senderId={}, contentId={}", batch.getSenderId(), batch.getContentId());
                }
                work(batch);
            } catch (QueryTimeoutException e) {
                // Lettuce 클라이언트는 기본적으로 1분후에 타임아웃 시킴
                // 서버의 안전성을 위해 작업큐에 task가 없다면
                // 1분(기본값)후에 연결을 재시도한 후 다시 블로킹
                log.info("[NOTIFICATION BLOCKING POP 재설정] NOTIFICATION 작업큐에 1분동안 작업이없어서 다시 연결합니다");
            } catch (RedisConnectionException e) {
                log.error("Redis 서버와의 연결이 끊어졌습니다. 다시 연결 시도 중...", e);
            } catch (Exception e) {
                log.error("메일 전송 작업 처리 중 예외가 발생했습니다.", e);
            }
        }
    }


    @Transactional
    @Override
    public void work(Object task) {
        NotificationBatchDto batch = (NotificationBatchDto) task;

        NotificationContent content =
                notificationCrudService.findContentById(batch.getContentId());

        User sender = userService.getUserById(content.getSenderId());

        List<NotificationV2> notifications = batch.getBatchRecipientIds().stream()
                .map(recipientId -> createNotification(recipientId, content))
                .collect(Collectors.toList());

        notifications.forEach(notification -> {
            ResponseNotificationDto message =
                    saveNotificationAndbuildMessage(notification, sender);
            publishMessage(message);
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

    private void publishMessage(ResponseNotificationDto message) {
        String channel = getNotificationChannelName(message.getTargetUserId());
        redisService.publishMessage(channel, message);
    }

    private String getNotificationChannelName(Long targetUserId) {
        return "notification." + targetUserId;
    }

    @Override
    public void enqueue(Object task) {
        if (task instanceof NotificationBatchDto) {
            redisService.pushTask(NOTIFICATION_QUEUE, task);
        } else {
            throw new UnsupportedTypeException(UNSUPPORTED_TYPE + task.getClass());
        }
    }
}
