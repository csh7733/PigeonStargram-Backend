package com.pigeon_stargram.sns_clone.event.redis.eventListener;

import com.pigeon_stargram.sns_clone.dto.chat.internal.NewChatDto;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis에서 채팅 메시지를 수신하는 리스너 클래스입니다.
 *
 * 이 클래스는 Redis로부터 수신된 채팅 메시지를 WebSocket을 통해
 * 해당 채팅방의 사용자들에게 전달합니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RedisChatMessageListener implements MessageListener {

    private final RedisService redisService;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        NewChatDto chatMessage = redisService.deserializeMessage(message.getBody(), NewChatDto.class);

        long user1Id = Math.min(chatMessage.getFrom(), chatMessage.getTo());
        long user2Id = Math.max(chatMessage.getFrom(), chatMessage.getTo());

        // 채팅방 경로를 생성하고 메시지를 해당 경로로 전송
        String destination = "/topic/chat/" + user1Id + "/" + user2Id;
        messagingTemplate.convertAndSend(destination, chatMessage);
    }
}
