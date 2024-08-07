package com.pigeon_stargram.sns_clone.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class WebSocketEventListener{

    private static final ConcurrentHashMap<Long, ConcurrentHashMap<Long, Boolean>> activeUsers = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Long> sessionUserMap = new ConcurrentHashMap<>();

    @EventListener
    public void handleSessionConnected(SessionConnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        Long userId = stringToLong(sha.getFirstNativeHeader("user-id"));
        Long partnerUserId = stringToLong(sha.getFirstNativeHeader("partner-user-id"));

        if (userId != null && partnerUserId != null) {
            sessionUserMap.put(sha.getSessionId(), userId);
            activeUsers.putIfAbsent(userId, new ConcurrentHashMap<>());
            activeUsers.get(userId).put(partnerUserId, true);
            log.info("User connected: {} -> is chatting with {}",userId,partnerUserId);
        }
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = sha.getSessionId();
        Long userId = sessionUserMap.remove(sessionId);


        if (userId != null) {
            ConcurrentHashMap<Long, Boolean> activeUser = activeUsers.get(userId);
            if (activeUser != null) {
                for (Long partnerUserId : activeUser.keySet()) {
                    activeUser.remove(partnerUserId);
                }
                if (activeUser.isEmpty()) {
                    activeUsers.remove(userId);
                }
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

    public static Boolean isUserChattingWith(Long userId, Long partnerUserId) {
        return activeUsers.containsKey(userId) && activeUsers.get(userId).getOrDefault(partnerUserId, false);
    }

}
