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

/**
 * WebSocket 연결 및 연결 해제 이벤트를 처리하는 리스너 클래스입니다.
 *
 * 이 클래스는 사용자가 WebSocket을 통해 연결되거나 연결 해제될 때 발생하는 이벤트를 처리하며,
 * Redis에 연결된 사용자 정보를 저장하거나 제거합니다. 또한 사용자 간 연결 이벤트를 발생시킵니다.
 */
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
            // Redis Hash에 세션 ID와 사용자 ID를 매핑하여 저장
            // key: sessionId, value: 사용자 ID
            redisService.putValueInHash(SESSION_USER_MAP_KEY, sha.getSessionId(), userId);

            // 두 사용자가 같은 채팅방에서 대화를 나누기 위한 설정
            // WebSocket 연결 시, Redis Hash에 자신의 사용자 ID와 상대방의 사용자 ID를 매핑하여 저장
            // HashKey: 나의 userId, fieldKey: 상대방의 userId, value: 접속 상태(true)
            String activeUsersKey = ACTIVE_USERS_KEY_PREFIX + userId;
            redisService.putValueInHash(activeUsersKey, String.valueOf(partnerUserId), true);

            // 사용자 연결 이벤트 발생
            eventPublisher.publishEvent(new UserConnectEvent(this, userId, partnerUserId));
        }
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = sha.getSessionId();
        Long userId = redisService.getValueFromHash(SESSION_USER_MAP_KEY, sessionId, Long.class);

        if (userId != null) {
            // Redis에서 세션 ID에 해당하는 사용자 ID 제거
            redisService.removeFieldFromHash(SESSION_USER_MAP_KEY, sessionId);

            // 두 사용자가 같은 채팅방에서 대화를 나누기 위한 설정
            // Redis에서 현재 사용자의 활성 사용자 목록에서 상대방 사용자 ID 제거
            // activeUsersKey는 현재 사용자의 userId를 기준으로 생성되며,
            // 해당 해시맵에 상대방 사용자 ID와 접속 상태가 저장되어 있음
            // 연결이 끊어지면 이 정보를 제거하여 상대방과의 연결 상태를 해제
            String activeUsersKey = ACTIVE_USERS_KEY_PREFIX + userId;
            Map<Object, Object> activeUsersMap = redisService.getAllFieldsFromHash(activeUsersKey);

            // 활성 사용자 목록에서 모든 상대방 ID에 대한 연결 상태 제거
            for (Object partnerUserId : activeUsersMap.keySet()) {
                redisService.removeFieldFromHash(activeUsersKey, String.valueOf(partnerUserId));
            }

            // 연결 종료
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
