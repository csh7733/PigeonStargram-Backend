package com.pigeon_stargram.sns_clone.event.redis;

import com.pigeon_stargram.sns_clone.event.redis.eventListener.*;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Redis 구독을 초기화하는 클래스입니다.
 *
 * 이 클래스는 Redis에서 특정 패턴의 메시지에 대해 다양한 리스너를 구독하며,
 * 각 리스너는 채팅 메시지, 온라인 상태, 마지막 메시지, 읽지 않은 채팅 수, 알림 등을 처리합니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RedisSubscriberInitializer {

    private final RedisService redisService;
    private final RedisChatMessageListener chatMessageListener;
    private final RedisOnlineStatusListener onlineStatusListener;
    private final RedisChatLastMessageListener chatLastMessageListener;
    private final RedisUnReadChatCountListener unReadChatCountListener;
    private final RedisNotificationListener notificationListener;

    @PostConstruct
    public void init() {
        redisService.subscribeToPattern("chat.*.*", chatMessageListener);  // 채팅 메시지 구독
        redisService.subscribeToPattern("user.online.status", onlineStatusListener);  // 온라인 상태 구독
        redisService.subscribeToPattern("lastMessage.chat.*.*", chatLastMessageListener);  // 마지막 채팅 메시지 구독
        redisService.subscribeToPattern("unreadChatCount.*", unReadChatCountListener);  // 읽지 않은 채팅 수 구독
        redisService.subscribeToPattern("notification.*", notificationListener);  // 알림 구독
    }
}
