package com.pigeon_stargram.sns_clone.controller.notification;

import com.pigeon_stargram.sns_clone.config.auth.annotation.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.dto.notification.response.ResponseNotificationDto;
import com.pigeon_stargram.sns_clone.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 알림과 관련된 API 요청을 처리하는 Controller 클래스입니다.
 * 사용자가 알림을 조회, 읽음 처리, 삭제 등의 작업을 할 수 있습니다.
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 현재 로그인한 사용자에 대한 모든 알림을 조회하는 메서드입니다.
     *
     * @param loginUser 현재 로그인한 사용자 (세션 정보)
     * @return 로그인 사용자의 알림 목록 (ResponseNotificationDto 리스트)
     */
    @GetMapping("")
    public List<ResponseNotificationDto> getNotifications(@LoginUser SessionUser loginUser) {
        Long loginUserId = loginUser.getId();

        return notificationService.findByUserId(loginUserId);
    }

    /**
     * 단일 알림에 대해 읽음 처리 요청을 수행하는 메서드입니다.
     *
     * @param loginUser 현재 로그인한 사용자 (세션 정보)
     * @param notificationId 읽음 처리할 알림의 ID
     */
    @PatchMapping("/{notificationId}/read")
    public void readNotification(@LoginUser SessionUser loginUser,
                                 @PathVariable Long notificationId) {

        notificationService.readNotification(notificationId);
    }

    /**
     * 단일 알림을 삭제하는 요청을 처리하는 메서드입니다.
     *
     * @param loginUser 현재 로그인한 사용자 (세션 정보)
     * @param notificationId 삭제할 알림의 ID
     */
    @DeleteMapping("/{notificationId}")
    public void deleteNotification(@LoginUser SessionUser loginUser,
                                   @PathVariable Long notificationId) {
        
        notificationService.deleteNotification(notificationId);
    }

    /**
     * 현재 로그인한 사용자의 모든 알림을 삭제하는 요청을 처리하는 메서드입니다.
     *
     * @param loginUser 현재 로그인한 사용자 (세션 정보)
     */
    @DeleteMapping("")
    public void deleteAllNotification(@LoginUser SessionUser loginUser) {

        notificationService.deleteAll(loginUser.getId());
    }

}
