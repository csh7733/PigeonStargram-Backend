package com.pigeon_stargram.sns_clone.repository.notification;

import com.pigeon_stargram.sns_clone.domain.notification.NotificationContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationContentRepository extends JpaRepository<NotificationContent, Long> {
}
