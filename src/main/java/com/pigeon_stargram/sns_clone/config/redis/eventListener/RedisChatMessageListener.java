package com.pigeon_stargram.sns_clone.config.redis.eventListener;

import com.pigeon_stargram.sns_clone.dto.chat.internal.NewChatDto;
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
public class RedisChatMessageListener implements MessageListener {

    private final RedisService redisService;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        NewChatDto chatMessage = redisService.deserializeMessage(message.getBody(), NewChatDto.class);
        log.info("메시지 = {}",chatMessage.toString());

        long user1Id = Math.min(chatMessage.getFrom(), chatMessage.getTo());
        long user2Id = Math.max(chatMessage.getFrom(), chatMessage.getTo());

        String destination = "/topic/chat/" + user1Id + "/" + user2Id;
        messagingTemplate.convertAndSend(destination, chatMessage);
    }
}
