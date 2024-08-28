package com.pigeon_stargram.sns_clone.event.redis.eventListener;

import com.pigeon_stargram.sns_clone.dto.chat.internal.SendLastMessageDto;
import com.pigeon_stargram.sns_clone.service.chat.ChatService;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisChatLastMessageListener implements MessageListener {

    private final RedisService redisService;
    private final ChatService chatService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        SendLastMessageDto sendLastMessageDto = redisService.deserializeMessage(message.getBody(), SendLastMessageDto.class);
        log.info("lastMessage = {}",sendLastMessageDto.toString());

        chatService.sentLastMessage(sendLastMessageDto);
    }
}
