package com.pigeon_stargram.sns_clone.repository.notification;

import com.pigeon_stargram.sns_clone.domain.notification.NotificationV2;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationV2Repository extends JpaRepository<NotificationV2, Long> {
}
