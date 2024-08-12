package com.pigeon_stargram.sns_clone.worker;

import com.pigeon_stargram.sns_clone.domain.notification.Notification;

public interface NotificationWorker {
    void work();
    void enqueue(Notification notification);
}
