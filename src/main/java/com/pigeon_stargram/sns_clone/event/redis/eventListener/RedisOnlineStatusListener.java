package com.pigeon_stargram.sns_clone.event.redis.eventListener;

import com.pigeon_stargram.sns_clone.dto.user.internal.UpdateOnlineStatusDto;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 * Redis에서 사용자 온라인 상태 메시지를 수신하는 리스너 클래스입니다.
 *
 * 이 클래스는 Redis로부터 수신된 사용자 온라인 상태 변경 메시지를 처리하여
 * UserService를 통해 사용자 상태를 업데이트합니다.
 */
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

        // 수신된 온라인 상태 업데이트를 UserService로 전달하여 처리
        userService.handleOnlineStatusUpdate(dto);
    }
}
