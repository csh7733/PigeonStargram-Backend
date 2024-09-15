package com.pigeon_stargram.sns_clone.worker.notification;

import com.pigeon_stargram.sns_clone.domain.notification.NotificationConvertable;
import com.pigeon_stargram.sns_clone.dto.notification.internal.NotificationBatchDto;
import com.pigeon_stargram.sns_clone.dto.notification.internal.NotificationSplitDto;
import com.pigeon_stargram.sns_clone.exception.redis.UnsupportedTypeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.constant.WorkerConstants.BATCH_SIZE;
import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.UNSUPPORTED_TYPE;

/**
 * 알림 분할 작업을 처리하는 워커 클래스입니다.
 *
 * 이 클래스는 대량의 수신자에게 알림을 보낼 때, 이를 작은 배치로 분할하여
 * 처리하는 역할을 수행합니다. 작업 큐에서 알림 작업을 가져와 분할한 후,
 * NotificationWorker에 작업을 위임합니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationSplitWorker implements NotificationWorker {

    private final NotificationWorker notificationWorker;
    private final BlockingQueue<NotificationSplitDto> beforeSplitQueue = new LinkedBlockingQueue<>();

    /**
     * Local 큐에서 알림 분할 작업을 지속적으로 처리하는 메서드입니다.
     *
     * 이 메서드는 블로킹 큐에서 작업을 가져와 분할한 후,
     * NotificationWorker에 분할된 작업을 위임합니다.
     */
    @Transactional
    @Override
    public void acceptTask() {
        while (true) {
            try {
                // Local 작업큐에서 Blocking Pop 방식으로 가져옴
                NotificationSplitDto beforeSplit = beforeSplitQueue.take();

                // 가져온 작업이 유효하다면 알림을 분할
                work(beforeSplit);
            } catch (Exception e) {
                log.error("알림 분할 작업 처리 중 예외가 발생했습니다.", e);
            }
        }
    }

    /**
     * 알림을 분할하고 분할된 작업을 NotificationWorker에 위임하는 메서드입니다.
     */
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

            // 수신자 목록에서 보낸 사람은 제외하고 배치 처리
            List<Long> batchRecipientIds = getBatchRecipientIds(recipientIds, leftIndex, rightIndex).stream()
                    .filter(recipientId -> !senderId.equals(recipientId))
                    .collect(Collectors.toList());

            NotificationBatchDto notificationBatchDto =
                    dto.toNotificationBatchDto(senderId, batchRecipientIds, contentId);

            notificationWorker.enqueue(notificationBatchDto);
        }
    }

    /**
     * 알림 분할 작업을 큐에 추가하는 메서드입니다.
     */
    @Override
    public void enqueue(Object task) {
        if (task instanceof NotificationSplitDto) {
            try {
                beforeSplitQueue.put((NotificationSplitDto) task);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new UnsupportedTypeException(UNSUPPORTED_TYPE + task.getClass());
        }
    }

    /**
     * 수신자 목록의 크기를 기준으로 분할 작업의 최대 반복 횟수를 계산하는 메서드입니다.
     *
     * @param recipientIds 수신자 목록
     * @return 분할 작업의 최대 반복 횟수
     */
    private static int getIterationMax(List<Long> recipientIds) {
        return (recipientIds.size() - 1) / BATCH_SIZE + 1;
    }

    /**
     * 주어진 인덱스 범위 내의 수신자 ID 목록을 추출하는 메서드입니다.
     *
     * @param recipientIds 전체 수신자 목록
     * @param leftIndex 추출할 시작 인덱스
     * @param rightIndex 추출할 종료 인덱스
     * @return 추출된 수신자 ID 목록
     */
    private static List<Long> getBatchRecipientIds(List<Long> recipientIds,
                                                   int leftIndex,
                                                   int rightIndex) {
        List<Long> subList = recipientIds.subList(leftIndex, rightIndex);
        List<Long> batchIds = new ArrayList<>(subList);
        return batchIds;
    }
}
