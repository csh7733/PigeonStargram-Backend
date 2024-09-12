package com.pigeon_stargram.sns_clone.repository.notification;

import com.pigeon_stargram.sns_clone.domain.notification.NotificationV2;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationV2Repository extends JpaRepository<NotificationV2, Long> {

    List<NotificationV2> findByRecipientId(Long recipientId);

    void deleteAllByRecipientId(Long recipientId);
}
