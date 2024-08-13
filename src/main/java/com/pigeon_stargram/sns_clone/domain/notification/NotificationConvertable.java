package com.pigeon_stargram.sns_clone.domain.notification;

import com.pigeon_stargram.sns_clone.domain.user.User;

import java.util.List;

public interface NotificationConvertable {

    Notification toNotification(User sender, User recipient);
    Long getSenderId();
    List<Long> getRecipientIds();
    String generateMessage(User sender, User recipient);
    String generateRedirectUrl(User sender, User recipient);
}
