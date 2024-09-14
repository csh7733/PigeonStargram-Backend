package com.pigeon_stargram.sns_clone.event.redis.eventListener;

import com.pigeon_stargram.sns_clone.dto.user.internal.UpdateOnlineStatusDto;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisOnlineStatusListener implements MessageListener {

    private final RedisService redisService;
    private final UserService userService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        UpdateOnlineStatusDto dto =
                redisService.deserializeMessage(message.getBody(), UpdateOnlineStatusDto.class);
        log.info("OnlineStatus = {}",dto.toString());

        userService.handleOnlineStatusUpdate(dto);
    }
}
