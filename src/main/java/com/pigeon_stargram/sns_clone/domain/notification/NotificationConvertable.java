package com.pigeon_stargram.sns_clone.domain.notification;

import com.pigeon_stargram.sns_clone.domain.user.User;

public interface NotificationConvertable {

    Notification toNotification(User sender, User recipient);
    Long getSenderId();
    Long getRecipientId();
    NotificationType getNotificationType(User sender, User recipient);
    String generateMessage(User sender, User recipient);
    String generateRedirectUrl(User sender, User recipient);
}
