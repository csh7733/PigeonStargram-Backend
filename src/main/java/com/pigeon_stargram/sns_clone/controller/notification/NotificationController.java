package com.pigeon_stargram.sns_clone.controller.notification;

import com.pigeon_stargram.sns_clone.config.auth.annotation.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationContent;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationV2;
import com.pigeon_stargram.sns_clone.dto.notification.response.ResponseNotificationDto;
import com.pigeon_stargram.sns_clone.repository.notification.NotificationContentRepository;
import com.pigeon_stargram.sns_clone.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
@RestController
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationContentRepository contentRepository;

    // 로그인유저에 대한 알림 조회
    @GetMapping("")
    public List<ResponseNotificationDto> getNotifications(@LoginUser SessionUser loginUser) {
        Long loginUserId = loginUser.getId();

        return notificationService.findUnreadNotifications(loginUserId);
    }

    // 단일 알림 읽음 처리요청
    @PatchMapping("/{notificationId}/read")
    public void readNotification(@LoginUser SessionUser loginUser,
                                 @PathVariable Long notificationId) {

        notificationService.readNotification(notificationId);
    }

    // 전체 알림 읽음 처리요청
    @PatchMapping("read")
    public void readNotifications(@LoginUser SessionUser loginUser) {
        Long loginUserId = loginUser.getId();

        notificationService.readNotifications(loginUserId);
    }

}
