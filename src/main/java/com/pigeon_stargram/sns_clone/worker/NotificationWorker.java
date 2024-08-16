package com.pigeon_stargram.sns_clone.worker;

import com.pigeon_stargram.sns_clone.domain.notification.Notification;
import com.pigeon_stargram.sns_clone.dto.notification.response.ResponseNotificationDto;
import com.pigeon_stargram.sns_clone.dto.notification.response.ResponseWebSocketNotificationDto;

public interface NotificationWorker {
    void work();
    void enqueue(ResponseWebSocketNotificationDto notification);
}
