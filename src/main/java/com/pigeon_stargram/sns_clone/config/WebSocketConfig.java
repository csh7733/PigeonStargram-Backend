package com.pigeon_stargram.sns_clone.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.SessionConnectEvent;

/**
 * WebSocket 설정을 위한 구성 클래스입니다.
 *
 * 이 클래스는 WebSocket 메시지 브로커 설정 및 STOMP 엔드포인트를 등록하여
 * 실시간 채팅, 알림 등 WebSocket 기반의 통신을 관리합니다.
 */
@Configuration
@EnableWebSocketMessageBroker
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * STOMP 엔드포인트를 등록하는 메서드입니다.
     *
     * 각 WebSocket 연결 경로를 설정하고, SockJS를 활성화합니다.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/chat")  // 채팅용 WebSocket 엔드포인트 등록
                .setAllowedOriginPatterns("*")
                .withSockJS();

        registry.addEndpoint("/ws/users")  // 사용자 정보용 WebSocket 엔드포인트 등록
                .setAllowedOriginPatterns("*")
                .withSockJS();

        registry.addEndpoint("/ws/notification")  // 알림용 WebSocket 엔드포인트 등록
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

}
