package com.pigeon_stargram.sns_clone.worker.notification;

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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.constant.RedisQueueConstants.NOTIFICATION_QUEUE;
import static com.pigeon_stargram.sns_clone.domain.notification.NotificationFactory.createNotification;
import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.UNSUPPORTED_TYPE;
import static com.pigeon_stargram.sns_clone.dto.notification.NotificationDtoConvertor.buildResponseNotificationDto;

/**
 * Redis 작업 큐에서 알림 전송 작업을 처리하는 워커 클래스입니다.
 *
 * 이 클래스는 Redis를 사용하여 알림 전송 작업을 처리하며,
 * 주어진 작업을 기반으로 알림을 생성하고 이를 대상 사용자에게 전송합니다.
 */
@Primary
@Component
@RequiredArgsConstructor
@Slf4j
public class RedisNotificationWorker implements NotificationWorker {

    private final RedisService redisService;
    private final NotificationCrudService notificationCrudService;
    private final UserService userService;

    /**
     * Redis 작업 큐에서 알림 전송 작업을 지속적으로 처리하는 메서드입니다.
     *
     * 작업이 없을 경우 Redis 블로킹 팝 방식으로 큐에서 작업을 대기하며,
     * 작업을 수신하면 이를 처리하고 알림을 전송합니다.
     */
    @Override
    public void acceptTask() {
        while (true) {
            try {
                // Redis 작업큐에서 Blocking Pop 방식으로 가져옴
                Object task = redisService.popTask(NOTIFICATION_QUEUE);
                if (task == null) {
                    throw new QueryTimeoutException("");
                } else if (!(task instanceof NotificationBatchDto)) {
                    throw new UnsupportedTypeException(UNSUPPORTED_TYPE + task.getClass());
                }

                NotificationBatchDto batch = (NotificationBatchDto) task;

                // 가져온 작업이 유효하다면 메일을 전송
                work(batch);
            } catch (QueryTimeoutException e) {
                // Lettuce 클라이언트의 기본 타임아웃(1분)에 도달하면 재연결 시도
            } catch (RedisConnectionException e) {
                log.error("Redis 서버와의 연결이 끊어졌습니다. 다시 연결 시도 중...", e);
            } catch (Exception e) {
                log.error("메일 전송 작업 처리 중 예외가 발생했습니다.", e);
            }
        }
    }

    /**
     * 주어진 알림 작업을 처리하는 메서드입니다.
     *
     * @param task 알림 작업 객체
     */
    @Transactional
    @Override
    public void work(Object task) {
        NotificationBatchDto batch = (NotificationBatchDto) task;

        // 알림 컨텐츠를 조회
        NotificationContent content =
                notificationCrudService.findContentById(batch.getContentId());
        User sender = userService.getUserById(content.getSenderId());

        // 수신자별로 알림을 생성하고 처리
        List<NotificationV2> notifications = batch.getBatchRecipientIds().stream()
                .map(recipientId -> createNotification(recipientId, content))
                .collect(Collectors.toList());

        notifications.forEach(notification -> {
            ResponseNotificationDto message =
                    saveNotificationAndbuildMessage(notification, sender);
            publishMessage(message);
        });

    }

    /**
     * 알림 작업을 큐에 추가하는 메서드입니다.
     *
     * @param task 큐에 추가할 알림 작업
     */
    @Override
    public void enqueue(Object task) {
        if (task instanceof NotificationBatchDto) {
            redisService.pushTask(NOTIFICATION_QUEUE, task);
        } else {
            throw new UnsupportedTypeException(UNSUPPORTED_TYPE + task.getClass());
        }
    }

    /**
     * 알림을 저장하고 메시지로 변환하는 메서드입니다.
     *
     * @param notification 저장할 알림 객체
     * @param sender 알림을 보낸 사용자
     * @return 생성된 알림 메시지
     */
    private ResponseNotificationDto saveNotificationAndbuildMessage(NotificationV2 notification,
                                                                    User sender) {
        NotificationV2 save = notificationCrudService.save(notification);
        NotificationContent saveContent = save.getContent();
        ResponseNotificationDto message =
                buildResponseNotificationDto(save, sender, saveContent);
        return message;
    }

    /**
     * 생성된 알림 메시지를 대상 사용자에게 전송하는 메서드입니다.
     *
     * @param message 전송할 알림 메시지
     */
    private void publishMessage(ResponseNotificationDto message) {
        String channel = getNotificationChannelName(message.getTargetUserId());
        redisService.publishMessage(channel, message);
    }

    /**
     * 대상 사용자의 알림 채널 이름을 생성하는 메서드입니다.
     *
     * @param targetUserId 대상 사용자 ID
     * @return 생성된 알림 채널 이름
     */
    private String getNotificationChannelName(Long targetUserId) {
        return "notification." + targetUserId;
    }
}
