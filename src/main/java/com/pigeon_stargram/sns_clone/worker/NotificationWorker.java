package com.pigeon_stargram.sns_clone.worker;

import com.pigeon_stargram.sns_clone.domain.notification.Notification;
import com.pigeon_stargram.sns_clone.dto.notification.response.ResponseNotificationDto;

public interface NotificationWorker {
    void startWorkers();
    void work();
    void enqueue(Object notification);
}
