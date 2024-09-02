package com.pigeon_stargram.sns_clone.event.webSocket;

import com.pigeon_stargram.sns_clone.event.user.UserConnectEvent;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.pigeon_stargram.sns_clone.constant.RedisUserConstants.ACTIVE_USERS_KEY_PREFIX;
import static com.pigeon_stargram.sns_clone.constant.RedisUserConstants.SESSION_USER_MAP_KEY;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener{

    private final RedisService redisService;
    private final ApplicationEventPublisher eventPublisher;

    @EventListener
    public void handleSessionConnected(SessionConnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        Long userId = stringToLong(sha.getFirstNativeHeader("user-id"));
        Long partnerUserId = stringToLong(sha.getFirstNativeHeader("partner-user-id"));

        if (userId != null && partnerUserId != null) {
            redisService.putValueInHash(SESSION_USER_MAP_KEY, sha.getSessionId(), userId);
            String activeUsersKey = ACTIVE_USERS_KEY_PREFIX + userId;
            redisService.putValueInHash(activeUsersKey, String.valueOf(partnerUserId), true);

            log.info("User connected: {} -> is chatting with {}",userId,partnerUserId);
            eventPublisher.publishEvent(new UserConnectEvent(this, userId, partnerUserId));
        }
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = sha.getSessionId();
        Long userId = redisService.getValueFromHash(SESSION_USER_MAP_KEY, sessionId, Long.class);


        if (userId != null) {
            redisService.removeFieldFromHash(SESSION_USER_MAP_KEY, sessionId);

            String activeUsersKey = ACTIVE_USERS_KEY_PREFIX + userId;
            Map<Object, Object> activeUsersMap = redisService.getAllFieldsFromHash(activeUsersKey);

            for (Object partnerUserId : activeUsersMap.keySet()) {
                redisService.removeFieldFromHash(activeUsersKey, String.valueOf(partnerUserId));
            }

            log.info("User disconnected: " + userId);
        }
    }

    private Long stringToLong(String header) {
        try {
            return header != null ? Long.valueOf(header) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
