package com.pigeon_stargram.sns_clone.event.redis;

import com.pigeon_stargram.sns_clone.event.redis.eventListener.RedisChatLastMessageListener;
import com.pigeon_stargram.sns_clone.event.redis.eventListener.RedisChatMessageListener;
import com.pigeon_stargram.sns_clone.event.redis.eventListener.RedisOnlineStatusListener;
import com.pigeon_stargram.sns_clone.event.redis.eventListener.RedisUnReadChatCountListener;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisSubscriberInitializer {

    private final RedisService redisService;
    private final RedisChatMessageListener chatMessageListener;
    private final RedisOnlineStatusListener onlineStatusListener;
    private final RedisChatLastMessageListener chatLastMessageListener;
    private final RedisUnReadChatCountListener unReadChatCountListener;

    @PostConstruct
    public void init() {
        redisService.subscribeToPattern("chat.*.*", chatMessageListener);
        redisService.subscribeToPattern("user.online.status", onlineStatusListener);
        redisService.subscribeToPattern("lastMessage.chat.*.*", chatLastMessageListener);
        redisService.subscribeToPattern("unreadChatCount.*", unReadChatCountListener);
    }
}
