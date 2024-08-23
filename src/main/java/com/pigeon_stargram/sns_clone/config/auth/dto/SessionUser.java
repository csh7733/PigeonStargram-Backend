package com.pigeon_stargram.sns_clone.config.auth.dto;

import com.pigeon_stargram.sns_clone.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@Getter
public class SessionUser implements Serializable {
    private Long id;
    private String name;
    private String email;
    private String picture;
}
