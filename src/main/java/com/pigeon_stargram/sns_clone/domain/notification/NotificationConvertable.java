package com.pigeon_stargram.sns_clone.domain.notification;

import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.notification.internal.NotificationBatchDto;

import java.util.List;

public interface NotificationConvertable {

    NotificationBatchDto toNotificationBatchDto(Long senderId, List<Long> batchRecipientIds, Long contentId);

    NotificationContent toNotificationContent();

    Long getSenderId();

    List<Long> toRecipientIds();

    String generateMessage();

}
