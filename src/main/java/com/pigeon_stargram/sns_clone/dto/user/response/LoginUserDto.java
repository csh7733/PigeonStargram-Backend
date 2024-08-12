package com.pigeon_stargram.sns_clone.dto.user.response;

import com.pigeon_stargram.sns_clone.domain.user.User;
import lombok.*;
@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserDto {
    private Boolean isLoggedIn;
    private Long userId;
    private String name;
    private String company;
    private String avatar;

    public LoginUserDto(User user) {
        isLoggedIn = true;
        this.userId = user.getId();
        this.name = user.getName();
        this.company = user.getCompany();
        this.avatar = user.getAvatar();
    }
}
