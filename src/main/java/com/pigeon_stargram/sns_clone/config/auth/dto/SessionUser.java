package com.pigeon_stargram.sns_clone.config.auth.dto;

import com.pigeon_stargram.sns_clone.domain.user.User;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Builder
@Getter
public class SessionUser implements Serializable {
    private Long id;
    private String name;
    private String email;
    private String picture;

    public SessionUser(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getWorkEmail();
        this.picture = user.getAvatar();
    }
}
