package com.pigeon_stargram.sns_clone.controller.notification;

import com.pigeon_stargram.sns_clone.config.auth.annotation.LoginUser;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.notification.response.ResponseNotificationDto;
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

    // 로그인유저에 대한 알림 조회
    @GetMapping("")
    public List<ResponseNotificationDto> getNotifications(@LoginUser User user) {
        return notificationService.findByUserId(user.getId()).stream()
                .map(notificationService::toResponseDto)
                .toList();
    }

    // 단일 알림 읽음 처리요청
    @PatchMapping("/{notificationId}/read")
    public ResponseNotificationDto readNotification(@LoginUser User user,
                                                    @PathVariable Long notificationId) {
        return notificationService.readNotification(notificationId);
    }

    // 전체 알림 읽음 처리요청
    @PatchMapping("read")
    public List<ResponseNotificationDto> readNotifications(@LoginUser User user) {
        return notificationService.readNotifications(user);
    }

}
