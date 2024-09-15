package com.pigeon_stargram.sns_clone.event.user;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 사용자가 연결되었을 때 발생하는 이벤트 클래스입니다.
 *
 * 이 클래스는 두 사용자가 연결될 때 발생하는 이벤트를 표현하며,
 * 사용자 ID와 상대방 사용자 ID를 포함합니다.
 */
@Getter
public class UserConnectEvent extends ApplicationEvent {
    private final Long userId;
    private final Long partnerUserId;

    public UserConnectEvent(Object source, Long userId, Long partnerUserId) {
        super(source);
        this.userId = userId;
        this.partnerUserId = partnerUserId;
    }
}
