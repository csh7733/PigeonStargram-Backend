package com.pigeon_stargram.sns_clone.repository.notification;

import com.pigeon_stargram.sns_clone.domain.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
