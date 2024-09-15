package com.pigeon_stargram.sns_clone.event.redis.eventListener;

import com.pigeon_stargram.sns_clone.dto.chat.internal.SendLastMessageDto;
import com.pigeon_stargram.sns_clone.service.chat.ChatService;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 * Redis에서 마지막 채팅 메시지를 수신하는 리스너 클래스입니다.
 *
 * 이 클래스는 Redis로부터 마지막 채팅 메시지를 수신하고, 이를 처리하여
 * ChatService를 통해 관련 작업을 수행합니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RedisChatLastMessageListener implements MessageListener {

    private final RedisService redisService;
    private final ChatService chatService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        SendLastMessageDto sendLastMessageDto = redisService.deserializeMessage(message.getBody(), SendLastMessageDto.class);

        // 수신된 마지막 메시지를 ChatService로 전달하여 처리
        chatService.sentLastMessage(sendLastMessageDto);
    }
}
