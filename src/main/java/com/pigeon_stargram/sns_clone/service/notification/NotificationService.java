package com.pigeon_stargram.sns_clone.service.notification;


import com.pigeon_stargram.sns_clone.domain.notification.Notification;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationConvertable;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.repository.notification.NotificationRepository;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import com.pigeon_stargram.sns_clone.worker.NotificationWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class NotificationService {

    private final UserService userService;
    private final NotificationRepository notificationRepository;
    private final NotificationWorker notificationWorker;

    public List<Notification> save(NotificationConvertable dto) {
        User sender = userService.findById(dto.getSenderId());
        List<Notification> notifications = dto.getRecipientIds().stream()
                .map(userService::findById)
                .map(recipient -> dto.toNotification(sender, recipient))
                .toList();
        notifications.forEach(notificationWorker::enqueue);
        return notificationRepository.saveAll(notifications);
    }

    public Optional<Notification> findById(Long id) {
        return notificationRepository.findById(id);
    }
}
