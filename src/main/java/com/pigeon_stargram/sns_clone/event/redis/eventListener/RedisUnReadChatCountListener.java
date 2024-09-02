package com.pigeon_stargram.sns_clone.event.redis.eventListener;

import com.pigeon_stargram.sns_clone.dto.chat.response.UnReadChatCountDto;
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
public class RedisUnReadChatCountListener implements MessageListener {

    private final RedisService redisService;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        UnReadChatCountDto unReadChatCountDto = redisService.deserializeMessage(message.getBody(), UnReadChatCountDto.class);
        String destination = "/topic/users/status/" + unReadChatCountDto.getToUserId();
        messagingTemplate.convertAndSend(destination, unReadChatCountDto);

        log.info("UnReadChatCount = {}", unReadChatCountDto.toString());
    }
}
