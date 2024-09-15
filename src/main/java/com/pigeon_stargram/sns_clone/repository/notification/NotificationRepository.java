package com.pigeon_stargram.sns_clone.repository.notification;

import com.pigeon_stargram.sns_clone.domain.notification.v1.NotificationV1;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationV1, Long> {

    List<NotificationV1> findAllByRecipientId(Long recipientId);
}
