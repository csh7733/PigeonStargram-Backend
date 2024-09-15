package com.pigeon_stargram.sns_clone.event.redis.eventListener;

import com.pigeon_stargram.sns_clone.dto.notification.response.ResponseNotificationDto;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis에서 알림 메시지를 수신하는 리스너 클래스입니다.
 *
 * 이 클래스는 Redis로부터 수신된 알림 메시지를 WebSocket을 통해
 * 해당 사용자에게 전달합니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RedisNotificationListener implements MessageListener {

    private final RedisService redisService;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        ResponseNotificationDto messageDto = redisService.deserializeMessage(message.getBody(), ResponseNotificationDto.class);

        // 알림을 보낼 경로를 설정하고 메시지를 해당 경로로 전송
        String destination = "/topic/notification/" + messageDto.getTargetUserId();
        messagingTemplate.convertAndSend(destination, messageDto);
    }
}
