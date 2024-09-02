package com.pigeon_stargram.sns_clone.event.user;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
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
