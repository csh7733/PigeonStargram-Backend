package com.pigeon_stargram.sns_clone.dto.login.response;

import lombok.*;
@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserDto {
    private Boolean isLoggedIn;
    private Long user;

    public LoginUserDto(Long userId) {
        isLoggedIn = true;
        this.user = userId;
    }
}
