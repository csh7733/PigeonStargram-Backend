package com.pigeon_stargram.sns_clone.domain.notification;

import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.notification.internal.NotificationBatchDto;

import java.util.List;

public interface NotificationConvertable {

    Notification toNotification(User sender,
                                User recipient);
    NotificationBatchDto toNotificationBatchDto(Long senderId,
                                                List<Long> batchRecipientIds);
    Long getSenderId();
    List<Long> getRecipientIds();
    String generateMessage();
    String generateRedirectUrl(User sender,
                               User recipient);
}
