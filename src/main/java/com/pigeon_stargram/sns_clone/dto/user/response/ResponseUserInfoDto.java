package com.pigeon_stargram.sns_clone.dto.user.response;

import com.pigeon_stargram.sns_clone.domain.user.User;
import lombok.*;

@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseUserInfoDto {
    private Long userId;
    private String name;
    private String avatar;

    public ResponseUserInfoDto(User user) {
        this.userId = user.getId();
        this.name = user.getName();
        this.avatar = user.getAvatar();
    }
}
