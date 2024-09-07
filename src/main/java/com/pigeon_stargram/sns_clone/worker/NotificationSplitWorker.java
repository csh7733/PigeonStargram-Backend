package com.pigeon_stargram.sns_clone.worker;

import com.pigeon_stargram.sns_clone.domain.notification.NotificationConvertable;
import com.pigeon_stargram.sns_clone.dto.notification.internal.NotificationBatchDto;
import com.pigeon_stargram.sns_clone.dto.notification.internal.NotificationSplitDto;
import com.pigeon_stargram.sns_clone.exception.redis.UnsupportedTypeException;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import io.lettuce.core.RedisConnectionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.constant.RedisQueueConstants.NOTIFICATION_SPLIT_QUEUE;
import static com.pigeon_stargram.sns_clone.constant.WorkerConstants.BATCH_SIZE;
import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.UNSUPPORTED_TYPE;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationSplitWorker implements NotificationWorker {

    private final NotificationWorker notificationWorker;

    private final RedisService redisService;

    private static int getIterationMax(List<Long> recipientIds) {
        return (recipientIds.size() - 1) / BATCH_SIZE + 1;
    }

    private static List<Long> getBatchRecipientIds(List<Long> recipientIds,
                                                   int leftIndex,
                                                   int rightIndex) {
        List<Long> subList = recipientIds.subList(leftIndex, rightIndex);
        List<Long> batchIds = new ArrayList<>(subList);
        return batchIds;
    }

    @Transactional
    @Override
    public void acceptTask() {
        while (true) {
            try {
                log.info("Redis 큐에서 알림 분할 작업을 대기 중입니다...");
                // Redis 작업큐에서 Blocking Pop 방식으로 가져옴
                Object task = redisService.popTask(NOTIFICATION_SPLIT_QUEUE);
                if (task == null) {
                    throw new QueryTimeoutException("");
                } else if (!(task instanceof NotificationSplitDto)) {
                    throw new UnsupportedTypeException(UNSUPPORTED_TYPE + task.getClass());
                }
                NotificationSplitDto beforeSplit = (NotificationSplitDto) task;

                // 가져온 작업이 유효하다면 메일을 전송
                log.info("분할할 알림을 가져왔습니다. 컨텐츠 ID: {}", beforeSplit.getContentId());
                work(beforeSplit);
            } catch (QueryTimeoutException e) {
                // Lettuce 클라이언트는 기본적으로 1분후에 타임아웃 시킴
                // 서버의 안전성을 위해 작업큐에 task가 없다면
                // 1분(기본값)후에 연결을 재시도한 후 다시 블로킹
                log.info("[NOTIFICATION BLOCKING POP 재설정] NOTIFICATION 작업큐에 1분동안 작업이없어서 다시 연결합니다");
            } catch (RedisConnectionException e) {
                log.error("Redis 서버와의 연결이 끊어졌습니다. 다시 연결 시도 중...", e);
            } catch (Exception e) {
                log.error("알림 분할 작업 처리 중 예외가 발생했습니다.", e);
            }
        }
    }

    @Override
    public void work(Object task) {
        NotificationSplitDto beforeSplit = (NotificationSplitDto) task;

        NotificationConvertable dto = beforeSplit.getNotificationDto();
        Long contentId = beforeSplit.getContentId();
        Long senderId = dto.getSenderId();

        List<Long> recipientIds = dto.toRecipientIds();
        int iterationMax = getIterationMax(recipientIds);
        for (int i = 0; i < iterationMax; i++) {

            int leftIndex = i * BATCH_SIZE;
            int rightIndex = (i == iterationMax - 1) ?
                    recipientIds.size() : (i + 1) * BATCH_SIZE;
            log.info("left={}, right={}", leftIndex, rightIndex);

            List<Long> batchRecipientIds = getBatchRecipientIds(recipientIds, leftIndex, rightIndex).stream()
                    .filter(recipientId -> !senderId.equals(recipientId))
                    .collect(Collectors.toList());

            NotificationBatchDto notificationBatchDto =
                    dto.toNotificationBatchDto(senderId, batchRecipientIds, contentId);

            notificationWorker.enqueue(notificationBatchDto);
        }
    }

    @Override
    public void enqueue(Object task) {
        if (task instanceof NotificationSplitDto) {
            redisService.pushTask(NOTIFICATION_SPLIT_QUEUE, task);
        } else {
            throw new UnsupportedTypeException(UNSUPPORTED_TYPE + task.getClass());
        }
    }
}
