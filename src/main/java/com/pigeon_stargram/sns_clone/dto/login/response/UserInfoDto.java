package com.pigeon_stargram.sns_clone.dto.login.response;

import com.pigeon_stargram.sns_clone.domain.user.User;
import lombok.*;

@ToString
@Getter
@NoArgsConstructor
public class UserInfoDto {
    private Long userId;
    private String name;
    private String company;
    private String avatar;

    public UserInfoDto(User user) {
        this.userId = user.getId();
        this.name = user.getName();
        this.company = user.getCompany();
        this.avatar = user.getAvatar();
    }
}
