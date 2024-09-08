package com.pigeon_stargram.sns_clone.event.redis.eventListener;

import com.pigeon_stargram.sns_clone.dto.notification.response.ResponseNotificationDto;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisNotificationListener implements MessageListener {

    private final RedisService redisService;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        ResponseNotificationDto messageDto = redisService.deserializeMessage(message.getBody(), ResponseNotificationDto.class);

        String destination = "/topic/notification/" + messageDto.getTargetUserId();
        messagingTemplate.convertAndSend(destination, messageDto);
        log.info("notification sent = {}", message);

    }
}
