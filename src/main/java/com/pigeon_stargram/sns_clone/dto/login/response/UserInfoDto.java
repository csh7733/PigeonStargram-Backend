package com.pigeon_stargram.sns_clone.dto.login.response;

import com.pigeon_stargram.sns_clone.domain.user.User;
import lombok.*;

@Builder
@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDto {
    private Long userId;
    private String name;
    private String company;
    private String avatar;
}
