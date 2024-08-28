package com.pigeon_stargram.sns_clone.config.redis;

import com.pigeon_stargram.sns_clone.config.redis.eventListener.RedisChatMessageListener;
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

    @PostConstruct
    public void init() {
        redisService.subscribeToPattern("chat.*.*", chatMessageListener);
    }
}
